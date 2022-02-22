function loadPieChartByChapter(chapter, dataset) {
// TODO: That's redundunt
    const svg = d3.select("#piechart");
    const textLabelSuffix = "%";
    const data = [];
    dataset
        .filter(function (el) {
            return el.byChapterMentions[chapter].length > 0;
        })
        .forEach((element) => {
            data.push({
                name: element.name,
                mentions: element.byChapterMentions[chapter],
            });
        });

    console.log("temp " + data.length + data);
    if (data.length === 0) {
        document.getElementById("message").innerHTML = "NO DATA";
    } else {
        document.getElementById("message").innerHTML = "Chapter " + (chapter + 1);
    }
    showPieChart(data, svg, textLabelSuffix);
}

function loadPieChart(dataset) {
    const svg = d3.select("#piechart");
    const textLabelSuffix = "%";
    const data = [];
    dataset
        .filter(function (el) {
            return el.mentions.length > 0;
        })
        .forEach((element) => {
            data.push({
                name: element.name,
                mentions: element.mentions,
            });
        });

    document.getElementById("message").innerHTML = "Whole Book";
    showPieChart(data, svg, textLabelSuffix);
}

function showPieChart(dataset, svg, textLabelSuffix) {
    // Clear Previous graph
    d3.selectAll("svg > *").remove();
    // d3.select("svg").remove();

    const container = d3.select("svg").classed("container", true);
    // .style('border', '1px solid red');
    const width = container.attr("width");
    const height = container.attr("height");

    const radius = 400;
    const g = container
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`);

    // const colour = d3.scaleOrdinal(['red', 'blue', 'green', 'gray'])
    const pie = d3
        .pie()
        // .sort(null)
        .value((d) => d.mentions.length);

    const path = d3.arc().outerRadius(radius).innerRadius(100);

    const label = d3
        .arc()
        .outerRadius(radius)
        .innerRadius(radius - 110);

    const pies = g
        .selectAll(".arc")
        .data(pie(dataset))
        .enter()
        .append("g")
        .attr("class", "arc");

    const color = d3.scaleOrdinal(d3.schemeSpectral[11]);
    pies
        .append("path")
        .attr("d", path)
        .attr("fill", function (d, i) {
            return color(i);
        });

    pies
        .append("text")
        .text((d) => d.data.name + "(" + d.data.mentions.length + ")")
        .attr("transform", (d) => `translate(${label.centroid(d)})`);
}
