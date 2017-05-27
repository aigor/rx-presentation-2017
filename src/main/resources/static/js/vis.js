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
    function progressing_data(data, vis_progress) {
        var data_part = data.map(function(e) { return e.slice(); });
        var max_size = data_part.length * data_part[0].length;

        for(var i = 1; i < data_part.length; i++) {
            var line = data_part[i];
            for(var j = 1; j < line.length; j++) {
                // Update value
                if (((i + 5) * (j + 3) % (i * j)) > (vis_progress / 100.0 * max_size)) {
                    line[j] = 0;
                }
            }
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

    // Chart control
    var cancel_btn = document.getElementById('cancel_vis');
    var reload_btn = document.getElementById('reload_vis');

    var query_prepare_delay = 100;
    var progress_delay = 200;

    var progress_enabled = true;
    var vis_progress = 0;

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
            vis_data(progressing_data(data, vis_progress));
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
        status("Preparing query for visualization...");
        setTimeout(progress, query_prepare_delay);
        vis_data(progressing_data(data, 0));
    };

    // Initial load
    initiate_progress();
}
