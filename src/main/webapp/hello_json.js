$(function() {
    $.ajax("/json", {
        contentType: "application/json",
        success: function(data) {
            $("body").append(data.result);
        }
    });
});