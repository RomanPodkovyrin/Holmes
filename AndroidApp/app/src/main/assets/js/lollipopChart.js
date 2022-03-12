width = 1000 - margin.left - margin.right;
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

    const svg = d3
        .select("svg")
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

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
        .style("text-anchor", "end")

    // Y axis
    const y = d3.scaleBand()
        .range([0, height])
        .domain(dataset.map(function (d) {
            return d.name;
        }))
        .padding(0.5);

    // Add Y axis to the left side
    svg.append("g")
        .call(d3.axisLeft(y))

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
        .attr("r", 15)
        .style("fill", "#ffffff")
        .attr("stroke-width", 10)
        .attr("stroke", "#19c4a0")


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
}
