console.log("Comment Module Included......");

let commentService = {
    getList: (bno, callback, error) => {
        $.getJSON("/comments/list/" + bno,
            function (list) {
                if (callback) {
                    callback(list);
                }
            }
        );
    },

    add: (comment, callback, error) =>{
        $.ajax({
            type: "POST",
            url: "/comments/new",
            data: JSON.stringify(comment),
            contentType: "application/json; charset=utf-8",
            success: callback,
            error: error
        });
    },

    remove: (cno, callback, error)=>{
        $.ajax({
            type: "DELETE",
            url: "/comments/"+cno,
            success: function (result, status, xhr){
                if(callback){
                    callback(result);
                }
            }
        });
    },

    update: (comment, callback, error) => {
        $.ajax({
            type: "PATCH",
            url: "/comments/"+comment.cno,
            data: comment.commentContent,
            contentType: "application/json; charset=utf-8",
            success: function (result, status, xhr) {
                if (callback) {
                    callback(result);
                }
            }
        });
    },
}