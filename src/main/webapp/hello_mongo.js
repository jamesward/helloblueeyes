$(function() {

    $("body").append('<h4>Bars:</h4>');
    $("body").append('<ul id="bars"></ul>');
    $("body").append('<input id="bar"/>');
    $("body").append('<button id="submit">GO!</button>');


    $("#submit").click(addbar);
    $("#bar").keyup(function(key) {
        if (key.which == 13) {
            addbar();
        }
    });

    loadbars();

});

function loadbars() {
    $.ajax("/mongo", {
        contentType: "application/json",
        success: function(data) {
            $("#bars").children().remove();
            $.each(data, function(index, item) {
                $("#bars").append("<li>" + item.name + "</li>");
            });
        }
    });
}

function addbar() {
    $.ajax({
        url: "/mongo",
        type: 'post',
        datatype: 'json',
        success: loadbars,
        data: '{"name": "' + $("#bar").val() + '"}',
        contentType: 'application/json'
    });
}