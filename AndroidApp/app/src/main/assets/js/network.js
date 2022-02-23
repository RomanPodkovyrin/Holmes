let width = 1000;
let height = 1500;

const margin = {
    top: 50, bottom: 50, left: 50, right: 50,
};

width = width - margin.right - margin.left;
height = height - margin.top - margin.bottom;

const spreadForce = -1050;
const spaceBetweenNodes = 40;

// Sorts Smallest to Larges
function compare(a, b) {
    if (a.value < b.value) {
        return -1;
    }
    if (a.value > b.value) {
        return 1;
    }
    return 0;
}


function plotNetwork(chapter, distances, characters, topMaxLinksPercentage) {
    console.log("Plotting network graph")

    // Clear Previous graph
    d3.selectAll("svg > *").remove();

    // create an svg to draw in
    const svg = d3
        .select("svg")
        .append("g")
        .attr("transform", "translate(" + margin.top + "," + margin.left + ")");


    let minValue = Number.MAX_VALUE;
    let maxValue = 0;
    const linkData = [];
    // Get distance values, sources and targets
    for (const [key, value] of Object.entries(distances[chapter])) {
        maxValue = Math.max(maxValue, value.tokenAverage);
        minValue = Math.min(minValue, value.tokenAverage);
        const names = key.split(",");
        linkData.push({
            value: value.tokenAverage, source: names[0], target: names[1],
        });
    }

    let acceptedMin = maxValue - (maxValue - minValue) * 1;
    let acceptedMax = minValue + (maxValue - minValue) * topMaxLinksPercentage;
    console.log("Max: " + maxValue + " Min: " + minValue + " acceptMin: " + acceptedMin + " acceptMax: " + acceptedMax);

    // Filter out elements according to accepted min and max
    const filteredLinkData = [];
    linkData
        .filter(function (element) {
            return element.value >= acceptedMin && element.value <= acceptedMax;
        })
        .forEach((element) => {
            filteredLinkData.push(element);
        });

    // Sort links in acceding order
    filteredLinkData.sort(compare);

    let minMentions = Number.MAX_VALUE;
    let maxMentions = 0

    const data = [];
    // Filter out characters if they are in this chapter
    characters
        .filter(function (element) {
            return element.byChapterMentions[chapter].length > 0;
        })
        .forEach((element) => {
            maxMentions = Math.max(maxMentions, element.byChapterMentions[chapter].length)
            minMentions = Math.min(minMentions, element.byChapterMentions[chapter].length)
            data.push({
                name: element.name, id: element.name, mentions: element.byChapterMentions[chapter].length
            });
        });

    // Get the nodes and links
    const nodes = data; //.nodes;
    const links = filteredLinkData//.splice(0, filteredLinkData.length * 1); //linkData //data.links;

    // linkColourScale.domain(d3.extent(links,function (d){
    //     return d.value
    // }))

    const linkWidthScale = d3
        .scaleLinear()
        .domain([acceptedMax, acceptedMin])
        //Controls the thickness of the link
        .range([0.1, 10]);
    const linkStrengthScale = d3
        .scaleLinear()
        .domain([acceptedMax, acceptedMin])
        .range([0, 0.009]);

    const nodeSizeScale = d3
        .scaleLinear()
        .domain([minMentions, maxMentions])
        .range([10, 100])

    const simulation = d3
        .forceSimulation()
        // Pulls nodes together based on their value
        .force("link", d3
            .forceLink()
            .id(function (d) {
                return d.id;
            })
            .strength(function (d) {
                return linkStrengthScale(d.value);
            }))

        // Spaces out nodes
        .force("charge", d3.forceManyBody().strength(spreadForce))
        // Collision detection
        .force("collide", function (d) {
            d3.forceCollide().radius(d.mentions * 2)//spaceBetweenNodes
        })
        // draw them in the middle of the screen
        .force("center", d3.forceCenter(width / 2, height / 2));


    const color = d3.scaleOrdinal(d3.schemeGreens[9]);
    let lineStrength = d3
        .scaleLinear()
        .domain([acceptedMax, acceptedMin])
        .range([1, 10]);

    // add the links
    const link = svg
        .selectAll(".link")
        .data(links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("stroke", function (d) {
            return color(lineStrength(d.value));
        })
        .attr("stroke-width", function (d) {
            //   console.log(
            //     "s: " +
            //       d.source +
            //       " d: " +
            //       d.target +
            //       " Width Value: " +
            //       linkWidthScale(d.value) +
            //       " d: " +
            //       d.value +
            //       " color: " +
            //       color(lineStrength(d.value))
            //   );
            return linkWidthScale(d.value);
        });

    // add the nodes
    const node = svg
        .selectAll(".node")
        .data(nodes)
        .enter()
        .append("g");

    // a circle to represent the node
    node
        .append("circle")
        .attr("class", "node")
        .attr("r", function (d) {
            // console.log(
            //     "Name: " +
            //       d.name +
            //       " Node Value: " +
            //       nodeSizeScale(d.mentions) +
            //       " mentions: " +
            //       d.mentions
            //   );
            return nodeSizeScale(d.mentions)//20
        })
        .attr("fill", function (d) {
            // Color of the node inside
            return "#15c092"; //d.colour;
        })
        //When overing over the node
        .on("mouseover", mouseOver(0.1))
        .on("mouseout", mouseOut);

    function mouseOver(opacity) {
        return function (_, d) {
            console.log("mouse over " + JSON.stringify(d));
            // check if nodes are connected.
            // if not then fade
            node.style("stroke-opacity", function (o) {
                return areNodesConnected(d, o) ? 1 : opacity;
            });
            node.style("fill-opacity", function (o) {
                return areNodesConnected(d, o) ? 1 : opacity;
            });

            link.style("stroke-opacity", function (o) {
                return o.source === d || o.target === d ? 1 : opacity;
            });
            // Change to this when hovered
            link.style("stroke", function (o) {
                // first colour is for focus colour
                // second is for the unfocus colour
                return o.source === d || o.target === d ? "#da0000" : "#1bb916";
            });
        };
    }

    function mouseOut() {
        // Reset back this when not
        link.style("stroke-opacity", 1);
        link.style("stroke", "#b6561a");
        node.style("fill-opacity", 1);
        node.style("stroke-opacity", 1);


    }


    // add a label to each node
    node
        .append("text")
        .attr("dx", 10) // horizontal distance from node
        .attr("dy", "0.23em") // vertical distance from node
        .text(function (d) {
            return d.name;
        })
        .style("stroke", "black")
        .style("stroke-width", 0.4)
        .style("fill", function (d) {
            // set text colour here
            return "#022e49"; //d.colour;
        });

    // Add nodes to simulation and give instructions for each tick
    simulation.nodes(nodes).on("tick", ticked);

    // Add links to simulation
    simulation.force("link").links(links);

    // on tick update node and link
    function ticked() {
        link.attr("d", updateLink);
        node.attr("transform", updateNodes);
    }

    // Draw links
    function updateLink(d) {
        return ("M" + d.source.x + "," + d.source.y + " " + d.target.x + "," + d.target.y);
    }

    // move the node based on physics
    function updateNodes(d) {
        // keep the elements within the boundaries
        //TODO: get radius to calucate the dist
        if (d.x < 0) d.x = 0;
        if (d.y < 0) d.y = 0;
        if (d.x > width) d.x = width;
        if (d.y > height) d.y = height;

        return "translate(" + d.x + "," + d.y + ")";
    }

    // Dictionary of links between notes
    const noteLinksDictionary = {};
    links.forEach(function (d) {
        noteLinksDictionary[d.source.id + "," + d.target.id] = 1;
    });

    // check the dictionary to see if nodes are linked
    function areNodesConnected(a, b) {
        return (a.id === b.id || noteLinksDictionary[a.id + "," + b.id] || noteLinksDictionary[b.id + "," + a.id]);
    }
}