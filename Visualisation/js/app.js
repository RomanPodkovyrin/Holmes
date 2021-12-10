//dummy data
const DUMMY_DATA = [
    {id: 'd1', value: 10, region:'USA'},
    {id: 'd2', value: 11, region:'India'},
    {id: 'd3', value: 12, region:'Russia'},
    {id: 'd4', value: 13, region:'Spain'},
    {id: 'd5', value: 7, region:'England'},
    {id: 'd6', value: 5, region:'Scotland'},

]

// d3.select('div')
//     .selectAll('p')
//     .data(DUMMY_DATA)
//     .enter() //give me all the missing elements (basically creates them if they dont exist for each data point)
//     .append('p') // bind data
//     .text(dta => dta.region + " Hello");

// Scaling function
const xScale = d3.scaleBand() // all bars need to have the same width
    .domain(DUMMY_DATA.map((dataPoint)=> dataPoint.region)) // to tell it how many items are on the axis
    .rangeRound([0,250] ) // from to
    .padding(0.1)
;
const yScale = d3.scaleLinear() // takes into account that height is different
    .domain([0, 20]) // min and max possible axis
    .range([200, 0]) // from to because the origin in the top left
//32


const container = d3.select('svg')
    .classed('container', true)
    // .style('border', '1px solid red');

container
    .selectAll('.bar')
    .data(DUMMY_DATA)
    .enter()
    .append('rect')
    .classed('bar', true)
    .attr('width', xScale.bandwidth())
    .attr('height', data =>  200 - yScale(data.value)) // 200 - because the origin is in the top left
    .attr('x', data => xScale(data.region)) // to space them apart
    .attr('y', data => yScale(data.value))
;