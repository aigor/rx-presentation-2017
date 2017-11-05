google.charts.load('current', {'packages':['bar']});
google.charts.setOnLoadCallback(processVisualization);

function processVisualization() {

    // UI part & useful methods

    var options = {
        chart: {
            title: 'Company Performance',
            subtitle: 'Sales, Expenses, and Profit: 2001-2017'
        },
        bars: 'vertical',
        vAxis: {format: 'decimal'},
        height: 400,
        colors: ['#1b9e77', '#d95f02', '#7570b3']
    };

    var chart = new google.charts.Bar(document.getElementById('chart_div'));

    function status(message) {
        document.getElementById('vis_status').textContent = "Status: " + message;
    }

    function chart_data(data) {
        return google.visualization.arrayToDataTable(data);
    }
    function vis_data(data){
        chart.draw(chart_data(data), google.charts.Bar.convertOptions(options));
    }

    // Chart control
    var cancel_btn = document.getElementById('cancel_vis');
    var reload_btn = document.getElementById('reload_vis');

    // Event processing
    if (typeof(EventSource) === "undefined") {
        status("Your browser does not support server-sent events.");
    }

    var initiate_progress = function() {
        console.log("Started new load session");

        var source = new EventSource("/sse");

        source.onclose = function () {
            console.log("SSE channel closed.");
            status("Communication channel closed");
            source.close();
        };

        reload_btn.onclick = function (e) {
            source.close();
            setTimeout(initiate_progress, 1);
        };

        source.onmessage = function (event) {
            var data = JSON.parse(event.data);
            console.log("New message: ", data)
            if (data.type === "progress") {
                status(data.message);
            } else if (data.type = "queryresult") {
                vis_data(data.data)
            }
        };

        cancel_btn.onclick = function (e) {
            source.close();
            status("Visualization cancelled");
        };
    };

    initiate_progress();
}
