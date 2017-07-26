
function drawScatterplotfromTable(fromTableID,TargetDivID,GeneInfoRestInterface) {
    //RETIVE DATA
    var Fieldnames = [];

    $("#" + fromTableID + " thead tr th").each(function (index, value) {

        Fieldnames.push($(value).text().trim())
    });

    //Creation of Data Table

    data = [];
    xvals = [];
    yvals = [];
    $("#" + fromTableID + " tbody tr ").each(function (index, value) {
        dataraw = [];
        $(value).children("td").each(function (index, value) {
            var val = $(value).text().trim()
            if (index == 0)
                xvals.push(parseFloat(val));
            else if (index == 1)
                yvals.push(parseFloat(val));

            dataraw.push(val)
        });
        data.push(dataraw)
    });

    // Brush Data

    var width = 700,
        height = 500,
        padding = 29.5;


    var svg = d3.select("#" + TargetDivID).append("svg")
        .attr('height', height)
        .attr('width', width);
    //Scale X scale Defintivon
    var x = d3.scale.linear()
        .domain(d3.extent(xvals))
        .range([ padding,width - padding]);


    var xaxis = svg.append('g')
        .attr("class", "y axis")
        .attr('transform', 'translate(0,' + (height - padding) + ')')
        .call(d3.svg.axis().scale(x));


    //Scale Y scale Defintivon
    var y = d3.scale.linear()
        .domain(d3.extent(yvals))
        .range([ height - padding,padding]);

    var yaxis = svg.append('g')
        .attr("class", "y axis")
        .attr('transform', 'translate(' + padding + ',0)')
        .call(d3.svg.axis().scale(y).orient("left"));

    //Scale for the Gene Length Stuff

    var minSinppetLength = d3.min(data, function (d) {
        return d[4] - d[3];
    });

    var maxSinppetLength = d3.max(data, function (d) {
        return d[4] - d[3];
    });

    var SnippetlengthScale = d3.scale.linear().domain([minSinppetLength, maxSinppetLength]).range([0, 1]);

/*    $.each(data,
        function (index, d) {
            var id = "point" + TargetDivID + (d[0] + "").replace(".", "_") + "_" + (d[1] + "").replace(".", "_");

            $("#" + id).mouseenter(
                    function () {
                    showGeneTip(d[2], d[3], d[4], id);
                });

            var wwatrt;
    });*/

    var postoStuff={};
    svg.selectAll("circle")
        .data(data)			//the meat of the example. Binds ui elems with data
        .enter()			//returns an arraylike object containing data that isn't
        // bound to the UI
        .append("circle")	// add ui elem and bind it to each data object
        //.attr("r", 2)		// set an attribute (again jQuery like)
        //.style("fill-opacity", 0.5)
        .attr("id", function (d) {
            var id = "point" + TargetDivID + (d[0] + "").replace(".", "_") + "_" + (d[1] + "").replace(".", "_")
            postoStuff[id]={chromosome: d[2], startPos: d[3], endPos: d[4]};
            return id;
        })
        .attr("r", function (d) {
            return SnippetlengthScale(d[4] - d[3]) + 1 * 3
        })		// set an attribute (again jQuery like)
        .style("fill-opacity", function (d) {
            return 1 - SnippetlengthScale(d[4] - d[3])
        })
        //using the scale objects above to properly place the circles in the graph
        .attr("cx", function (d) {
            return x(d[0]);
        })
        .attr("cy", function (d) {
            return y(d[1]);
        });


    $.each($("#"+TargetDivID + " circle"), function( index, element) {$(element).qtip({

        style: {classes: 'qtip-light qtip-rounded'},
        position: {
            my: 'top left',
            at: 'bottom right'
        },
        content: {
            text: 'Loading...',

            ajax:{
        url: GeneInfoRestInterface, // URL to the local file
            type: 'POST', // POST or GET
            "data": {chromosome: element.__data__[2], startPos: element.__data__[3], endPos: element.__data__[4] },
        success: function(Infos) {
            // Process the data

            // Set the content manually (required!)
            this.set('content.text', Infos);
        }
    }
}});});



/*    svg.selectAll("circle")
        .data(data).each(
        function(d,i){
            var AiDee = "point" + TargetDivID + (d[0] + "").replace(".", "_") + "_" + (d[1] + "").replace(".", "_");
            $("#"+AiDee).qtip({

                style: {classes: 'qtip-light qtip-rounded'},
                position: {
                    my: 'top left',
                    at: 'bottom right'
                },
            content: {
                text: 'Loading...'

/!*                ajax:{
                    url: '../../Gene/getByPosition', // URL to the local file
                    type: 'GET', // POST or GET
                    data: {chromosome: d[2], startPos: d[3], endPos: d[4]}, // Data to pass along with your request
                    success: function(Infos, status) {
                        // Process the data

                        // Set the content manually (required!)
                        $("#"+AiDee).set('content.text', Infos);
                    }
                }*!/
            }});});*/
/*
        .on("mouseenter", function(d) { showGeneTip(d[2],d[3],d[4],"point"+TargetDivID+(d[0]+"").replace(".","_")+"_"+(d[1]+"").replace(".","_") );});
*/
}