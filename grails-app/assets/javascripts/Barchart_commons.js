//Modded from d3 Website

function CountBarChart(fromTableID,Into,valuename, countName ){
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


    var pointofInterestVal =-1;
    var pointofInterestCount =-1;
    //Get Indices

    $("#"+fromTableID+" thead tr th").each(function(index,value){
        if($(value).text() == valuename){
            pointofInterestVal = index;
        }
        if(countName && $(value).text() == countName){
            pointofInterestCount = index;
        }
    });

    dataraw = [];
    datatemp ={};
    all = 0.0;
    //CheckTable and Accumulate
    $("#"+fromTableID+" tbody tr ").each(function(index,value){
        var count =0;
        var val ="";
        var done = false;
         $(value).children("td").each(function(index,value){
            if(pointofInterestCount>=0){
                if(index == pointofInterestCount){
                    count = parseInt($(value).text().trim())
                }
            }else
                count =1;

            if (index == pointofInterestVal)
                val =$(value).text().trim();

            if(done == false && val != "" && count>0){

                done =true;
                if(!(val in datatemp))
                    datatemp[val] =0;

                datatemp[val] = datatemp[val] +count;
                all+=count;
            }


        });
    });



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