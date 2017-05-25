google.charts.load('current', {'packages':['bar']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
    var data = google.visualization.arrayToDataTable([
        ['Year', 'Sales', 'Expenses', 'Profit', 'NRT'],
        ['2010', 1000, 500, 800, 247],
        ['2011', 1170, 460, 252, 225],
        ['2012', 960, 1120, 300, 245],
        ['2013', 1030, 340, 352, 645],
        ['2014', 1000, 400, 200, 1145],
        ['2015', 1170, 460, 250, 255],
        ['2016', 960, 1120, 800, 275],
        ['2017', 1030, 340, 350, 215],
        ['2018', 1000, 500, 800, 247],
        ['2019', 1170, 460, 252, 225],
        ['2020', 960, 1120, 300, 245],
        ['2021', 1030, 340, 352, 645],
        ['2022', 1000, 400, 200, 1145],
        ['2023', 1170, 460, 250, 255],
        ['2024', 960, 1120, 800, 275],
        ['2025', 1030, 340, 350, 215]
    ]);

    var options = {
        chart: {
            title: 'Company Performance',
            subtitle: 'Sales, Expenses, and Profit: 2014-2017',
        },
        bars: 'vertical',
        vAxis: {format: 'decimal'},
        height: 400,
        colors: ['#1b9e77', '#d95f02', '#7570b3']
    };

    var chart = new google.charts.Bar(document.getElementById('chart_div'));

    chart.draw(data, google.charts.Bar.convertOptions(options));

    // var btns = document.getElementById('btn-group');
    //
    // btns.onclick = function (e) {
    //
    //     if (e.target.tagName === 'BUTTON') {
    //         options.vAxis.format = e.target.id === 'none' ? '' : e.target.id;
    //         chart.draw(data, google.charts.Bar.convertOptions(options));
    //     }
    // }
}
