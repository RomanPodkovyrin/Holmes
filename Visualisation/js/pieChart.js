const DUMMY_DATA = [
    {name: 'Chrome', value: 10},
    {name: 'Edge', value: 1},
    {name: 'Safari', value: 2},
    {name: 'FireFox', value: 5}]

// loadPieChart(DUMMY_DATA)
function loadPieChart(dataset) {
    var svg = d3.select("#piechart");
    var height = 900;
    var width = 500;
    var textLabelSuffix = "%";

    showPieChart(dataset, svg, height, width,
        textLabelSuffix);
}

function showPieChart(dataset, svg, height, width,
                      textLabelSuffix) {
    const container = d3.select('svg')
        .classed('container', true)
// .style('border', '1px solid red');
    width = container.attr('width')
    height = container.attr('height')

    const radius = 200
    const g = container.append('g')
        .attr('transform', `translate(${width / 2}, ${height / 2})`)

// const colour = d3.scaleOrdinal(['red', 'blue', 'green', 'gray'])
    const pie = d3.pie()
        // .sort(null)
        .value(d => d.value)

    const path = d3.arc()
        .outerRadius(radius)
        .innerRadius(100)

    const label = d3.arc()
        .outerRadius(radius)
        .innerRadius(radius - 110)

    const pies = g.selectAll('.arc')
        .data(pie(dataset))
        .enter()
        .append('g')
        .attr('class', 'arc')

    const color = d3.scaleOrdinal(d3.schemeCategory10);
    pies.append('path')
        .attr('d', path)
        .attr("fill", function (d, i) {
            return color(i);
        })

    pies.append('text')
        .text(d => d.data.name)
        .attr('transform', d => `translate(${label.centroid(d)})`)
}
