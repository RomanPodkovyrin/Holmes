let width = 1000;
let height = 1200;

const margin = {
    top: 50, bottom: 50, left: 90, right: 90,
};

function updateSvgSize() {
    const container = d3.select("svg").classed("container", true);
    width = container.attr("width");
    height = container.attr("height");
    console.log("Width: " + width + " height: " + height)
    width = width - margin.right - margin.left;
    height = height - margin.top - margin.bottom;
    console.log("With margin, Width: " + width + " height: " + height)
}

const spreadForce = -1050;
const spaceBetweenNodesMultiplier = 1;

// Sorts Smallest to Larges
function compareLinks(a, b) {
    if (a.value < b.value) {
        return -1;
    }
    if (a.value > b.value) {
        return 1;
    }
    return 0;
}


// Sorts Largest to Smallest
function compareNodes(a, b) {
    if (a.mentions < b.mentions) {
        return 1;
    }
    if (a.mentions > b.mentions) {
        return -1;
    }
    return 0;
}

// Weighting for each accepted punctuation
const punctuationWeight = new Map();

punctuationWeight.set('.', 10);
punctuationWeight.set('?', 10);
punctuationWeight.set('!', 10);
punctuationWeight.set(':', 1);
punctuationWeight.set(';', 1);
punctuationWeight.set(',', 1)


/**
 * Checks if the link for the character exists
 */
function doesLinkExist(data, source, target) {
    return data.find(characters => characters.name === source) && data.find(characters => characters.name === target);
}

let minMentions = Number.MAX_VALUE;
let maxMentions = 0

function getCharactersFromChapter(characters, chapter, nodeData) {

    // Filter out characters if they are not in this chapter
    characters
        .filter(function (element) {
            return element.byChapterMentions[chapter].length > 0;
        })
        .forEach((element) => {
            maxMentions = Math.max(maxMentions, element.byChapterMentions[chapter].length)
            minMentions = Math.min(minMentions, element.byChapterMentions[chapter].length)
            nodeData.push({
                name: element.name, id: element.name, mentions: element.byChapterMentions[chapter].length
            });
        });
    return {maxMentions, minMentions};
}

let minDistanceValue = Number.MAX_VALUE;
let maxDistanceValue = 0;

function getLinkData(distances, chapter, nodeData, linkData) {

    // Get distance values, sources and targets
    let distance;
    for (const [key, value] of Object.entries(distances[chapter])) {
        const names = key.split(",");
        if (nodeData.find(characters => characters.name === names[0]) && nodeData.find(characters => characters.name === names[1])) {


            //TODO: make a getDistance function with choice parameter
            distance = 0//value.tokenAverage
            Object.entries(value.averagePunctuationDistance).forEach(([key, value]) => {
                // console.log("each " + key + " v:" + value)
                distance += punctuationWeight.get(key) * value
            })
            console.log("Distance: " + distance)
            distance = Math.pow(distance, exponent)
            console.log("Exponent: " + distance)
            maxDistanceValue = Math.max(maxDistanceValue, distance);
            minDistanceValue = Math.min(minDistanceValue, distance);

            linkData.push({
                value: distance, source: names[0], target: names[1],
            });
        }
    }
    return {maxDistanceValue, minDistanceValue};
}

// Controls how spread out the values are
const exponent = 2
const maxLinkStrength = 0.009

function plotNetwork(chapter, distances, characters, topLinksPercentage, topCharactersByMentions) {
    console.log("Plotting network graph with parameters")
    console.log("Chapter: " + chapter + " topLinksPercentage: " + topLinksPercentage + " topCharactersByMentions: " + topCharactersByMentions)
    updateSvgSize()

    //const topMinLinksPercentage = 1


    // Clear Previous graph
    d3.selectAll("svg > *").remove();

    // create a svg to draw in
    const svg = d3
        .select("svg")
        .append("g")
        .attr("transform", "translate(" + margin.top + "," + margin.left + ")");


    let nodeData = [];
    const maxAndMinMentions = getCharactersFromChapter(characters, chapter, nodeData);
    maxMentions = maxAndMinMentions.maxMentions;
    minMentions = maxAndMinMentions.minMentions;

    nodeData.sort(compareNodes);
    nodeData = nodeData.slice(0, nodeData.length * topCharactersByMentions)


    const linkData = [];
    const maxAndMinDistances = getLinkData(distances, chapter, nodeData, linkData);
    maxDistanceValue = maxAndMinDistances.maxDistanceValue;
    minDistanceValue = maxAndMinDistances.minDistanceValue;

    let acceptedDistanceMin = minDistanceValue//(topMinLinksPercentage === 1) ? minDistanceValue : maxDistanceValue - (maxDistanceValue - minDistanceValue) * topMinLinksPercentage;
    let acceptedDistanceMax = maxDistanceValue//(topLinksPercentage === 1) ? maxDistanceValue : minDistanceValue + (maxDistanceValue - minDistanceValue) * topLinksPercentage;
    console.log("Max: " + maxDistanceValue + " Min: " + minDistanceValue + " acceptMin: " + acceptedDistanceMin + " acceptMax: " + acceptedDistanceMax);

    // Filter out elements according to accepted min and max
    let filteredLinkData = [];
    linkData
        .filter(function (element) {
            return element.value >= acceptedDistanceMin && element.value <= acceptedDistanceMax && doesLinkExist(nodeData, element.source, element.target);
        })
        .forEach((element) => {
            filteredLinkData.push(element);
        });

    // Sort links in acceding order
    filteredLinkData.sort(compareLinks);
    // Remove links based on the control parameter topLinksPercentage
    filteredLinkData = filteredLinkData.slice(0, filteredLinkData.length * topLinksPercentage)

    // Update max an min values
    maxDistanceValue = 0
    minDistanceValue = Number.MAX_VALUE
    filteredLinkData.forEach((element) => {
        maxDistanceValue = Math.max(maxDistanceValue, element.value);
        minDistanceValue = Math.min(minDistanceValue, element.value);
    })
    acceptedDistanceMax = maxDistanceValue
    acceptedDistanceMin = minDistanceValue

    // Get the nodes and links
    const nodes = nodeData;
    const links = filteredLinkData
    drawCharacterNetwork(acceptedDistanceMax, acceptedDistanceMin, svg, links, nodes);
}


function drawCharacterNetwork(acceptedDistanceMax, acceptedDistanceMin, svg, links, nodes) {
    const linkWidthScale = d3
        .scaleLinear()
        .domain([acceptedDistanceMax, acceptedDistanceMin])
        //Controls the thickness of the link
        .range([0.5, 25]);
    const linkStrengthScale = d3
        .scaleLinear()
        .domain([acceptedDistanceMax, acceptedDistanceMin])
        .range([0, maxLinkStrength]);

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
            d3.forceCollide().radius(nodeSizeScale(d.mentions) * spaceBetweenNodesMultiplier)
        })
        // draw them in the middle of the screen
        .force("center", d3.forceCenter(width / 2, height / 2));


    let lineStrength = d3
        .scaleLinear()
        .domain([acceptedDistanceMax, acceptedDistanceMin])
        .range([0, 1]);

    // add the links
    const link = svg
        .selectAll(".link")
        .data(links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("stroke", function (d) {
            return d3.interpolateRdYlGn(lineStrength(d.value));
        })
        .attr("stroke-width", function (d) {
            console.log("Link " + d.source + " to " + d.target + " value: " + d.value + ", width: " + linkWidthScale(d.value))
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
            return nodeSizeScale(d.mentions)
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
                // second is to unfocus the colour
                return o.source === d || o.target === d ? d3.interpolateReds(lineStrength(o.value)) : "#1bb916";
            });
        };
    }

    function mouseOut() {
        // Reset back this when not
        link.style("stroke-opacity", 1);
        link.style("stroke", function (d) {
            return d3.interpolateRdYlGn(lineStrength(d.value));
        });
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
