$(function () {
    var source = $("#entry-template").html();
    var template = Handlebars.compile(source);
    var $results = $('.results'),
        $searchButton = $('#search button'),
        $searchQuery = $('#search input');

    var packagesResourceUrl = 'http://hackathon-mule-repository.herokuapp.com/api/packages';

    function displayPackages( data, query ) {
        $results.html(template({items: data, query: query}));
    }

    function findPackages( query ) {
        var url = packagesResourceUrl;

        if ( query ) {
            url += '?q=' + encodeURIComponent(query);
        }

        return $.getJSON(url)
    }


    $searchQuery.keydown(function ( evt ) {
        if ( evt.keyCode === 13 ) {
            $searchButton.click();
        }
    }).focus();
    $searchButton.click(function () {
        var query = $searchQuery.val();
        findPackages(query).then(function (data) {
            displayPackages(data, query);
        });
    });
});



