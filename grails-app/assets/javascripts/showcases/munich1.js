

function createVisualisationMunich1(dataFileLocation,elementForVisualisation){

    // Set properties such as width and height for Scatter plot and Pie Chart
    var SPmargin = {top: 20, right: 20, bottom: 30, left: 40},
        SPwidth = 900 - SPmargin.left - SPmargin.right,
        SPheight = 400 - SPmargin.top - SPmargin.bottom;

    // create svg for scatter plot
    var SPsvg = d3.select(elementForVisualisation).append("svg")
        .attr("width", SPwidth + SPmargin.left + SPmargin.right)
        .attr("height", SPheight + SPmargin.top + SPmargin.bottom)
        .append("g")
        .attr("transform", "translate(" + SPmargin.left + "," + SPmargin.top + ")");

    // Set properties  for Pie Chart
    var PIEmargin = {top: 20, right: 20, bottom: 30, left: 10}
    pieDim ={w:250, h: 250, rpadding:200},
        pieDim.r = Math.min(pieDim.w, pieDim.h) / 2;

    // create svg for pie chart
    var PIEsvg = d3.select(elementForVisualisation).append("svg")
        .attr("width", pieDim.w+pieDim.rpadding)
        .attr("height", 400)
        .append("g")
        .attr("transform", "translate(" + PIEmargin.left + "," + PIEmargin.top + ")")

    // color scale for the Pie chart. Categorical. One color for each mitochondrial function
    var color = d3.scale.category20()

    // color scale for the Scatter plot. Linear. According to the expression value of gene
    var color2 = d3.scale.linear()
        .range(["red", "green"])
        .interpolate(d3.interpolateHsl)

    // color for genes with mutations on scatter plot
    var mutatedcolor = "blue";
    var highlightcolor = "#f0027f";
    var highlightradius = 6.5;



// function to create pie chart
function PieChart(datahandle){

    var PC={};

    // function to draw the arcs of the pie slices.
    var arc = d3.svg.arc().outerRadius(pieDim.r - 10).innerRadius(0);

    // function to compute the pie slice angles.
    var pie = d3.layout.pie().sort(null).value(function(d) { return d.count; });

    // bind data
    d3.csv(datahandle, function(error, csvdata) {
        if (error) throw error;

        // group elements (each entry) by mitochondrial function
        var data = d3.nest()
            .key(function(d) { return d.func;})
            .entries(csvdata);

        // count number of elements of each mitochondrial function to draw the piechart
        data.forEach(function(d) {
            d.func = d.key;
            d.count = d.values.length;
        });

        // append pie chart svg and bind data that is transformed by "pie" function to give slice angles
        var g = PIEsvg.append("g")
            .attr("transform", "translate("+pieDim.w/2+","+pieDim.h/2+")")
            .selectAll(".arc")
            .data(pie(data))
            .enter().append("g")
            .attr("class", "arc");

        // draw the pie with "arc" function
        g.append("path")
            .attr("d", arc)
            .style("fill", function(d) { return color(d.data.func); })
            .on("mouseover",mouseover);

        // draw legends
        var legend = PIEsvg.selectAll(".legend")
            .data(color.domain())
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

        legend.append("rect")
            .attr("x", pieDim.w+10)
            .attr("width", 18)
            .attr("height", 18)
            .style("fill", color);

        legend.append("text")
            .attr("x", pieDim.w+32)
            .attr("y", 9)
            .attr("dy", ".35em")
            .style("text-anchor", "start")
            .text(function(d) { return d; });

        //function to update the scatter plot when mouseover event occurs by calling SP.update
        function mouseover(d){
            SP.update(d.data.func, color(d.data.func));
        }

    });

    return PC;
}

// function to create scatter plot
function ScatterPlot(datahandle,elementForVisualisation){

    var SP={};

    // Construct a ordinal scale for x-axis with range equals to width of the svg and with an empty domain
    var x = d3.scale.ordinal()
        .rangeRoundPoints([0,SPwidth],1);

    // Construct a linear scale for y-axis with range equals to height of the svg and with an empty domain
    var y = d3.scale.linear()
        .range([SPheight, 0]);

    // create x-axis
    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    // create y-axis
    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left");

    // create tooltip box that gives information of a node
    var div = d3.select(elementForVisualisation).append("div")
        .attr("class", "tooltip")
        .style("opacity", 0);

    // bind data
    d3.csv(datahandle, function(error, csvdata) {
        if (error) throw error;

        var data = [];

        // Set "Apoptosis" as the default function to be displayed on scatter plot
        csvdata.forEach(function(d) {
            if(d.func== "Apoptosis" && !isNaN(parseFloat(d.value)) && isFinite(d.value)) data.push(d);
        });

        data.forEach(function(d) {
            d.value= +d.value;
        });

        // Give domain to x-axis, which corresponds to each sample
        x.domain(data.map(function(d) {return d.sample}));

        // Give domain to y-axis and the linear color scale, which corresponds to the range between absolute values of the maximum gene expression value
        var ymin = Math.abs(d3.min(data, function(d) { return d.value; }));
        var ymax = Math.abs(d3.max(data, function(d) { return d.value; }));
        var yabs = Math.max(ymin,ymax);
        y.domain([yabs*-1, yabs]);

        color2.domain([yabs*-1, yabs]);

        // append axis labels and legends
        SPsvg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + SPheight/2 + ")")
            .call(xAxis)
            .append("text")
            .attr("class", "label")
            .attr("x", SPwidth)
            .attr("y", -6)
            .style("text-anchor", "end")
            .text("Sample");

        SPsvg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("class", "label")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Log2 Fold change");

        SPsvg.append("rect")
            .attr("class","SPrect")
            .attr("x", SPwidth-180)
            .attr("width", 18)
            .attr("height", 18)
            .style("fill", "#1f77b4");

        SPsvg.append("text")
            .attr("class","SPtitle")
            .text("Apoptosis")
            .attr("transform", "translate("+(SPwidth-155)+",13)");

        // create mapped data
        var nodedata = data.map(function(d) {
            return {
                x: x(d.sample),
                y: y(d.value),
                r: 3.5,
                value: d.value,
                sample: d.sample,
                func: d.func,
                gene: d.gene,
                mutation: d.mutation}
        });

        // bind mapped data to the nodes (circles) to be drawn on scatter plot
        var nodes = SPsvg.selectAll("circle.node")
            .data(nodedata);

        // draw nodes. Create mouseover function, which 1. gives a tooltip box displaying info of that gene; 2. update color of nodes by calling SP.update2
        nodes.enter().append("circle")
            .attr("class", "node")
            .attr("r", function(d) { return d.mutation !== "NULL" ? highlightradius : d.r })
            .attr("cx", function(d) { return d.x })
            .attr("cy", function(d) { return d.y })
            .style("fill", function(d){return d.mutation !== "NULL" ? mutatedcolor : color2(d.value)})
            .style("stroke", "black")
            .style("stroke-width", 0.5)
            .on("mouseover", function(d) {
                var muts = d.mutation.split("|");
                var muttext = "<br>";
                for (i = 0; i < muts.length; i++) {
                    muttext += muts[i] + "<br>";
                }
                tooltipheight = (53+muts.length*13).toString()+"px";
                div.transition()
                    .duration(200)
                    .style("opacity", .9)
                    .style("height", tooltipheight);
                div.html("Gene: " + d.gene + "<br>" +
                        "Function: " + d.func + "<br>"+
                        "Log2 Fold Change: " + d.value + "<br>" +
                        "Mutation: " + muttext)
                    .style("left", (d3.event.pageX+5) + "px")
                    .style("top", (d3.event.pageY - 10) + "px");
                SP.update2(d.gene)
            })
            .on("mouseout", function(d) {
                div.transition()
                    .duration(500)
                    .style("opacity", 0);
                SP.update2("NULL");
            });

        // give a beeswarm-like scatter plot
        var norm = d3.random.normal(0, 1.5);

        var iterations = 0;

        while(iterations++ < 100) {
            var q = d3.geom.quadtree(nodedata);

            for(var i = 0; i < nodedata.length; i++)
                q.visit(collide(nodedata[i]))
        }

        nodes.transition()
            .attr("cx", function(d) { return d.x });

        function collide(node) {
            var r = node.r + 16,
                nx1 = node.x - r,
                nx2 = node.x + r,
                ny1 = node.y - r,
                ny2 = node.y + r;
            return function(quad, x1, y1, x2, y2) {
                if (quad.point && (quad.point !== node)) {
                    var x = node.x - quad.point.x,
                        y = node.y - quad.point.y,
                        l = Math.sqrt(x * x + y * y),
                        r = node.r + quad.point.r;
                    if (l < r)
                        node.x += norm()
                }
                return x1 > nx2
                    || x2 < nx1
                    || y1 > ny2
                    || y2 < ny1
            }
        }

        // function to update the scatter plot when mouseover event occurs on pie chart
        SP.update = function(nfunc, ncolor){

            // bind new data, update domains, enter/update/remove nodes. scripts same as above
            var newdata=[];

            csvdata.forEach(function(d) {
                if(d.func==nfunc && !isNaN(parseFloat(d.value)) && isFinite(d.value)) {newdata.push(d);}
            });

            newdata.forEach(function(d) {
                d.value = +d.value;
            });

            //Update scale domains
            var ymin = Math.abs(d3.min(newdata, function(d) { return d.value; }));
            var ymax = Math.abs(d3.max(newdata, function(d) { return d.value; }));
            var yabs = Math.max(ymin,ymax);
            y.domain([yabs*-1, yabs]);

            color2.domain([yabs*-1, yabs]);

            // update legends
            SPsvg.selectAll("text.SPtitle").text(nfunc);
            SPsvg.selectAll("rect.SPrect").style("fill",ncolor);

            var newnodedata = newdata.map(function(d) {
                return {
                    x: x(d.sample),
                    y: y(d.value),
                    r: 3.5,
                    value: d.value,
                    sample: d.sample,
                    func: d.func,
                    gene: d.gene,
                    mutation: d.mutation}
            });

            var newnodes = SPsvg.selectAll("circle.node")
                .data(newnodedata);

            iterations = 0;

            while(iterations++ < 100) {
                var q = d3.geom.quadtree(newnodedata);

                for(var i = 0; i < newnodedata.length; i++)
                    q.visit(collide(newnodedata[i]));
            }

            //Update all circles
            newnodes.transition()
                .duration(1000)
                .attr("r", function(d){return d.mutation !== "NULL" ? highlightradius : d.r })
                .attr("cx", function(d) { return d.x; })
                .attr("cy", function(d) { return d.y; })
                .style("fill", function(d) {return d.mutation !== "NULL" ? mutatedcolor : color2(d.value)});

            //Enter new circles
            newnodes.enter().append("circle")
                .attr("class", "node")
                .attr("r", function(d){return d.mutation !== "NULL" ? highlightradius : d.r })
                .attr("cx", function(d) { return d.x; })
                .attr("cy", function(d) { return d.y; })
                .style("fill", function(d) {return d.mutation !== "NULL" ? mutatedcolor : color2(d.value)})
                .style("stroke", "black")
                .style("stroke-width", 0.5)
                .on("mouseover", function(d) {
                    var muts = d.mutation.split("|");
                    var muttext = "<br>";
                    for (i = 0; i < muts.length; i++) {
                        muttext += muts[i] + "<br>";
                    }
                    tooltipheight = (53+muts.length*13).toString()+"px";
                    div.transition()
                        .duration(200)
                        .style("opacity", .9)
                        .style("height", tooltipheight);
                    div.html("Gene: " + d.gene + "<br>" +
                            "Function: " + d.func + "<br>"+
                            "Log2 Fold Change: " + d.value + "<br>" +
                            "Mutation: " + muttext)
                        .style("left", (d3.event.pageX+5) + "px")
                        .style("top", (d3.event.pageY - 10) + "px");
                    SP.update2(d.gene)
                })
                .on("mouseout", function(d) {
                    div.transition()
                        .duration(500)
                        .style("opacity", 0);
                    SP.update2("NULL");
                });

            // Remove old circles
            newnodes.exit()
                .transition(1000)
                .attr("r", 0)
                .remove();

            SPsvg.select(".y.axis")
                .transition()
                .duration(1000)
                .call(yAxis);

        }
        // function to update scatter plot when mouseover event occurs on scatter plot itself
        SP.update2 = function(ingene){

            // update the color of nodes
            SPsvg.selectAll("circle.node")
                .transition()
                .duration(500)
                .style("fill", function(d) {return d.gene == ingene ? highlightcolor : d.mutation !== "NULL" ? mutatedcolor : color2(d.value)})
                .attr("r", function(d) {return d.gene == ingene ? highlightradius : d.mutation !== "NULL" ? highlightradius : d.r })

        }

    });

    return SP;
}

    var SP=ScatterPlot(dataFileLocation,elementForVisualisation);
    var PC=PieChart(dataFileLocation);


}