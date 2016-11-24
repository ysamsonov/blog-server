var obj = {
    login: "123",
    name: "Yura",
    surname: "Sam",
    email: "123@mail.ru",
    password: "123",
    avatar: {
        id:5
    }
};
var test = function () {
    $.ajax({
        url: 'api/account',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify(obj),
        processData: false,
        success: function (data, textStatus, jQxhr) {
            console.log(data);
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(jqXhr.responseJSON);
        }
    });
};

test();