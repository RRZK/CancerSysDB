//Stolen from d3.js Website

function ChromosomeCountBarChart(fromTableID, Into){
    var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = 600 - margin.left - margin.right,
        height = 300 - margin.top - margin.bottom;

    var x = d3.scale.ordinal()
        .rangeRoundBands([0, width], .1);

    var y = d3.scale.linear()
        .range([height, 0]);

    var chromosome = d3.svg.axis()
        .scale(x)
        .orient("bottom");

    var occ = d3.svg.axis()
        .scale(y)
        .orient("left")
        .ticks(10, "%");

    var svg = d3.select("#"+Into).append("svg:svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


        var pointofInterest =-1;

    $("#"+fromTableID+" thead tr th").each(function(index,value){
        if($(value).text() == "chromosome"){
            pointofInterest = index;

        }

    });

    dataraw = [];
    $("#"+fromTableID+" tbody tr ").each(function(index,value){
         $(value).children("td").each(function(index,value){

            if (index == pointofInterest)
                dataraw.push($(value).text().trim())
        });
    });

    datatemp ={};


    for( i =1; i< 23;i++){
        datatemp[(i+" ").trim()]=0.0;

    }
    datatemp["X"]=0.0;

    datatemp["Y"]=0.0;


    all = 0.0;
    for( i =0; i< dataraw.length;i++){
        datatemp[dataraw[i]] = datatemp[dataraw[i]] +1;
        all = all + 1.0;
    }
    data= [];


    for(a in datatemp) {
        data.push({chromosome: a, occ: (datatemp[a]/all), occAbs:datatemp[a]});

    }


        x.domain(data.map(function(d) {
            return d.chromosome;
        }));
        y.domain([0, d3.max(data, function(d) { return d.occ })]);

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(chromosome);



        svg.selectAll(".bar")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function(d) { return x(d.chromosome); })
            .attr("width", x.rangeBand())
            .attr("y", function(d) { return y(d.occ); })
            .attr("height", function(d) { return height - y(d.occ); });

        svg.selectAll(".labels")
            .data(data).enter()
            .append("text")

            .text(function(d) { if( d.occAbs>0)
                return d.occAbs;
            else
                return ""; })
            .attr("x", function(d) { return x(d.chromosome) +(x.rangeBand() *.75); })

            .attr("y", function(d) { return y(d.occ); })

            .attr("dy", "1em")
            .style("text-anchor", "end");

    svg.append("g")
        .attr("class", "y axis")
        .call(occ)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Precent");

}

function type(d) {
    d.occ = +d.occ;
    return d;
}