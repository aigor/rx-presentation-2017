google.charts.load('current', {'packages':['bar']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
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

    var data = [
        ['Year', 'Sales', 'Expenses', 'Profit', 'NRT'],
        ['2001', 300, 130, 45, 123],
        ['2002', 400, 50, 190, 347],
        ['2003', 490, 60, 252, 90],
        ['2004', 480, 30, 496, 467],
        ['2005', 1030, 340, 352, 645],
        ['2006', 1000, 400, 200, 1145],
        ['2007', 1170, 460, 250, 255],
        ['2008', 960, 1120, 800, 275],
        ['2009', 1030, 340, 350, 215],
        ['2010', 1000, 500, 800, 247],
        ['2011', 1170, 460, 252, 225],
        ['2012', 960, 1120, 300, 245],
        ['2013', 1030, 340, 352, 645],
        ['2014', 1000, 400, 200, 1145],
        ['2015', 1170, 460, 250, 255],
        ['2016', 960, 1120, 800, 275],
        ['2017', 200, 110, 330, 60]
    ];

    // Supporting functions
    function data_size(data){
        return (data.length - 1) * (data[0].length - 1)
    }

    function range(start, count) {
        return Array.apply(0, new Array(count))
            .map(function (element, index) {
                return index + start;
            });
    }

    function shuffle(a) {
        var j, x, i;
        for (i = a.length; i; i--) {
            j = Math.floor(Math.random() * i);
            x = a[i - 1];
            a[i - 1] = a[j];
            a[j] = x;
        }
    }

    function progressing_data(data, vis_progress, appearance_order) {
        var data_part = data.map(function(e) { return e.slice(); });
        var max_size = data_size(data_part);
        var row_size = data_part.length - 1;

        var vis_position = Math.round(vis_progress / 100.0 * max_size);

        for(var i = vis_position; i < appearance_order.length; i++) {
            var elem = appearance_order[i];
            var k = Math.floor(elem / row_size) + 1;
            var j = (elem % row_size) + 1;
            data_part[j][k] = 0;
        }
        return data_part;
    }
    
    function chart_data(data) {
        return google.visualization.arrayToDataTable(data);
    }

    function status(message) {
        document.getElementById('vis_status').textContent = "Status: " + message;
    }

    var chart = new google.charts.Bar(document.getElementById('chart_div'));
    function vis_data(data){
        chart.draw(chart_data(data), google.charts.Bar.convertOptions(options));
    }

    function randomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    function generateOrder(data){
        var ord = range(0, data_size(data));
        shuffle(ord);
        return ord;
    }

    // Chart control
    var cancel_btn = document.getElementById('cancel_vis');
    var reload_btn = document.getElementById('reload_vis');

    var query_prepare_delay = 100;
    var progress_delay = 200;

    var progress_enabled = true;
    var vis_progress = 0;
    var appearance_order = [];

    cancel_btn.onclick = function (e) {
        progress_enabled = false;
        status("Visualization cancelled");
    };

    reload_btn.onclick = function (e) {
        progress_enabled = true;
        initiate_progress();
    };

    var progress = function() {
        if (progress_enabled) {
            var step = randomInt(0, 10);
            vis_progress = (vis_progress + step < 100) ? vis_progress + step : 100;
            vis_data(progressing_data(data, vis_progress, appearance_order));
            status(vis_progress + "% of data is loaded");
            if (vis_progress < 100) {
                setTimeout(progress, progress_delay + randomInt(10, progress_delay));
            } else {
                status("Done!");
            }
        }
    };

    var initiate_progress = function() {
        vis_progress = 0;
        appearance_order = generateOrder(data);
        status("Preparing query for visualization...");
        setTimeout(progress, query_prepare_delay);
        vis_data(progressing_data(data, 0, appearance_order));
    };

    // Initial load
    initiate_progress();
}
