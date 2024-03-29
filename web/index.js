function draw(container, dataLinks, toolTips) {
    
    const width = 928;
    const height = 600;
    const types = Array.from(new Set(dataLinks.map(d => d.type)));
    const nodes = Array.from(new Set(dataLinks.flatMap(l => [l.source, l.target])), id => ({id}));
    const links = dataLinks.map(d => Object.create(d))
    const tips = toolTips.reduce((acc, obj) => 
        {  acc[obj.definition] = obj.tip;  return acc;}, {});
    
    const color = d3.scaleOrdinal(types, d3.schemeCategory10);
    
    const simulation = d3
    .forceSimulation(nodes)
    .force("link", d3.forceLink(links).id(d => d.id))
    .force("charge", d3.forceManyBody().strength(-600))
    .force("x", d3.forceX())
    .force("y", d3.forceY());
    
    const svg = d3
    .select(container)
    .append("svg")
    .attr("viewBox", [-width / 2, -height / 2, width, height])
    .attr("width", width)
    .attr("height", height)
    .attr("style", "max-width: 100%; height: auto; font: 12px sans-serif;");
    

    var tooltip = d3.select("#tooltip")
    .append("div")
    .style("position", "fixed")
    .style("visibility", "hidden")
    .style("right", "5%");

    svg
    .append("defs")
    .selectAll("marker")
    .data(types)
    .join("marker")
    .attr("id", d => `arrow-${d}`)
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 15)
    .attr("refY", -0.5)
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("orient", "auto")
    .append("path")
    .attr("fill", color)
    .attr("d", "M0,-5L10,0L0,5");
    
    const link = svg.append("g")
    .attr("fill", "none")
    .attr("stroke-width", 1.5)
    .selectAll("path")
    .data(links)
    .join("path")
    .attr("stroke", d => color(d.type))
    .attr("marker-end", d => `url(${new URL(`#arrow-${d.type}`, location)})`);
    
    const node = svg.append("g")
    .attr("fill", "currentColor")
    .attr("stroke-linecap", "round")
    .attr("stroke-linejoin", "round")
    .selectAll("g")
    .data(nodes)
    .join("g")
    .call(drag(simulation));
    
    node.append("circle")
    .attr("stroke", "white")
    .attr("stroke-width", 1.5)
    .attr("r", 4)
    .on("mouseover", function(){tooltip.style("visibility", "visible");})
    //.on("mouseout", function(){tooltip.style("visibility", "hidden");})
    .on("mousemove", function(event){
        return tooltip
        .html("<u><h3>" + event.target.__data__.id + "</h3></u><br/>" + 
                tips[event.target.__data__.id]);
        //.style("top", (event.pageY+100)+"px")
        //.style("left",(event.pageX+100)+"px");
    });
    
    node.append("text")
    .attr("x", 8)
    .attr("y", "0.51em")
    .text(d => d.id)
    .clone(true).lower()
    .attr("fill", "none")
    .attr("stroke", "white")
    .attr("stroke-width", 3);
    
    simulation.on("tick", () => {
        link.attr("d", linkArc);
        node.attr("transform", d => `translate(${d.x},${d.y})`);
    });
}

function linkArc(d) {
    const r = Math.hypot(d.target.x - d.source.x, d.target.y - d.source.y);
    return `
    M${d.source.x},${d.source.y}
    A${r},${r} 0 0,1 ${d.target.x},${d.target.y}
    `;
}

drag = simulation => {
    
    function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }
    
    function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
    }
    
    function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
    
    return d3.drag()
    .on("start", dragstarted)
    .on("drag", dragged)
    .on("end", dragended);
}