function loadPieChartByChapter(chapter, dataset) {
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

    showPieChart(data);
}

function loadPieChart(dataset) {
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

    showPieChart(data);
}

function showPieChart(dataset) {
    // Clear Previous graph
    d3.selectAll("svg > *").remove();

    const container = d3.select("svg").classed("container", true);
    const width = container.attr("width");
    const height = container.attr("height");

    const radius = 400;
    const g = container
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`);

    const pie = d3
        .pie()
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
