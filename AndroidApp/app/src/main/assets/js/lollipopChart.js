width = 1000 - margin.left - margin.right - 200;
height = 1300 - margin.top - margin.bottom;


const animationDuration = 1000

function loadLollipop(dataset) {
    console.log("Loading Lollipop Chart for the whole book")
    const data = [];
    dataset
        .filter(function (el) {
            return el.mentions.length > 0;
        })
        .forEach((element) => {
            data.push({
                name: element.name,
                mentions: element.mentions.length,
            });
        });

    plotLollipop(data);
}

function loadLollipopByChapter(chapter, dataset) {
    console.log("Loading Lollipop Chart for Chapter: " + chapter)
    const data = [];
    dataset
        .filter(function (el) {
            return el.byChapterMentions[chapter].length > 0;
        })
        .forEach((element) => {
            data.push({
                name: element.name,
                mentions: element.byChapterMentions[chapter].length,
            });
        });

    plotLollipop(data);
}

function plotLollipop(dataset) {
    console.log("Plotting Lollipop Graph")

    console.log("Plotting Lollipop on width: " + width + " and height: " + height)
    // Clear Previous graph
    d3.selectAll("svg > *").remove();

    let maxMentions = 0;
    dataset
        .forEach((element) => {
            maxMentions = Math.max(maxMentions, element.mentions)
        });

    // sort data
    dataset.sort(function (b, a) {
        return a.mentions - b.mentions;
    });

    tempLeftMargin = margin.left + 220
    const svg = d3
        .select("svg")
        .append("g")
        .attr("transform", "translate(" + tempLeftMargin + "," + margin.top + ")");

    // X axis
    const x = d3.scaleLinear()
        .domain([0, maxMentions])
        .range([0, width]);

    // Add X axis to the bottom
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x))
        .selectAll("text")
        .attr("transform", "translate(-10,0)rotate(-45)")
        .style("font-size", "3.0em")
        .attr("font-weight", 700)
        .style("text-anchor", "end")

    // Y axis
    const y = d3.scaleBand()
        .range([0, height])
        .domain(dataset.map(function (d) {
            return d.name;
        }))
        .padding(1);

    svg.append("g")
        .attr("transform", "translate(0," + 10 + ")")
        .call(d3.axisLeft(y))
        .selectAll("text")
        .attr("transform", "translate(0,-10)rotate(0)")
        .style("font-size", "3.0em")
        .attr("font-weight", 700)
        .style("text-anchor", "end");


    // Mention Bars
    svg.selectAll("mentionBar")
        .data(dataset)
        .enter()
        .append("line")
        .attr("x1", x(0))
        .attr("x2", x(0))
        .attr("y1", function (d) {
            return y(d.name);
        })
        .attr("y2", function (d) {
            return y(d.name);
        })
        .attr("stroke", "#19c4a0")
        .attr("stroke-width", 5)

    // Lollipop circle
    const circle
        = svg.selectAll("endCircle")
        .data(dataset)
        .enter()
        .append("circle")
        .attr("cx", x(0)) // Start at 0
        .attr("cy", function (d) {
            return y(d.name);
        })
        .attr("r", 6)
        .style("fill", "#ffffff")
        .attr("stroke-width", 5)
        .attr("stroke", "#19c4a0")

    const label = svg.selectAll("endCircle")
        .data(dataset)
        .enter()
        .append("text")
        .style("font-size", "2.0em")
        .attr("font-weight", 700)
        .attr("dx", function(d){return 0})
        .attr("dy", function(d) {return y(d.name)+10})
        .text(function(d){return d.mentions})


    // Animation - moving lines and circle to their final location
    svg.selectAll("line")
        .transition()
        .duration(animationDuration)
        .attr("x1", function (d) {
            return x(d.mentions);
        })

    svg.selectAll("circle")
        .transition()
        .duration(animationDuration)
        .attr("cx", function (d) {
            return x(d.mentions);
        })

   svg.selectAll("text")
        .transition()
        .duration(animationDuration)
        .attr("dx", function (d) {
            return x(d.mentions) + 10;
        })
}
