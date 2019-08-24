//初始化需要显示的图片，并且指定显示的位置
window.addEventListener("load", showPage);
function clickLink(){
    var objs = document.getElementsByTagName("a");
    for(var i=0;i<objs.length;i++)
    {
        objs[i].onclick=function()
        {
            window.imagelistener.openUrl(this.href); return false;
            //通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
        }
    }
}

function clickImage() {

    var objs = document.getElementsByTagName("img");
    for(var i=0;i<objs.length;i++) {
        var url = objs[i].getAttribute('src');

        objs[i].onclick=function() {
            //如果是加载失败的情况下，点击则重新加载
            var status = this.getAttribute("load");
            if (status === "fail"){
                //点击重新加载
                singleImageLoad(this,this.id);
            }else if (status === "success"){
                //打开图片弹窗
                window.imagelistener.openImage(this.src);
            }
        }

    }
}

function imagesLoad() {
    var imglist = document.getElementsByTagName('img');
    for (i = 0; i < imglist.length; i++) {
        singleImageLoad(imglist[i],"img0" + i);

    }
}

function singleImageLoad(object,id) {
    object.id = id;
    object.style.cssText="margin: 0 auto;";

//    object.src = "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJsb2FkZXItMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA1MCA1MCIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNTAgNTA7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4KPHBhdGggZmlsbD0iIzAwMCIgZD0iTTI1LjI1MSw2LjQ2MWMtMTAuMzE4LDAtMTguNjgzLDguMzY1LTE4LjY4MywxOC42ODNoNC4wNjhjMC04LjA3MSw2LjU0My0xNC42MTUsMTQuNjE1LTE0LjYxNVY2LjQ2MXoiIHRyYW5zZm9ybT0icm90YXRlKDU5LjM5MjggMjUgMjUpIj4KPGFuaW1hdGVUcmFuc2Zvcm0gYXR0cmlidXRlVHlwZT0ieG1sIiBhdHRyaWJ1dGVOYW1lPSJ0cmFuc2Zvcm0iIHR5cGU9InJvdGF0ZSIgZnJvbT0iMCAyNSAyNSIgdG89IjM2MCAyNSAyNSIgZHVyPSIwLjZzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIvPgo8L3BhdGg+Cjwvc3ZnPg==";
    object.src = "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJsb2FkZXItMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiIHdpZHRoPSI0MHB4IiBoZWlnaHQ9IjQwcHgiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNDAgNDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPgo8cGF0aCBvcGFjaXR5PSIwLjIiIGZpbGw9IiMwMDAiIGQ9Ik0yMC4yMDEsNS4xNjljLTguMjU0LDAtMTQuOTQ2LDYuNjkyLTE0Ljk0NiwxNC45NDZjMCw4LjI1NSw2LjY5MiwxNC45NDYsMTQuOTQ2LDE0Ljk0NiYjMTA7ICAgIHMxNC45NDYtNi42OTEsMTQuOTQ2LTE0Ljk0NkMzNS4xNDYsMTEuODYxLDI4LjQ1NSw1LjE2OSwyMC4yMDEsNS4xNjl6IE0yMC4yMDEsMzEuNzQ5Yy02LjQyNSwwLTExLjYzNC01LjIwOC0xMS42MzQtMTEuNjM0JiMxMDsgICAgYzAtNi40MjUsNS4yMDktMTEuNjM0LDExLjYzNC0xMS42MzRjNi40MjUsMCwxMS42MzMsNS4yMDksMTEuNjMzLDExLjYzNEMzMS44MzQsMjYuNTQxLDI2LjYyNiwzMS43NDksMjAuMjAxLDMxLjc0OXoiLz4KPHBhdGggZmlsbD0iIzAwMCIgZD0iTTI2LjAxMywxMC4wNDdsMS42NTQtMi44NjZjLTIuMTk4LTEuMjcyLTQuNzQzLTIuMDEyLTcuNDY2LTIuMDEyaDB2My4zMTJoMCYjMTA7ICAgIEMyMi4zMiw4LjQ4MSwyNC4zMDEsOS4wNTcsMjYuMDEzLDEwLjA0N3oiIHRyYW5zZm9ybT0icm90YXRlKDEzMS43MjEgMjAgMjApIj4KPGFuaW1hdGVUcmFuc2Zvcm0gYXR0cmlidXRlVHlwZT0ieG1sIiBhdHRyaWJ1dGVOYW1lPSJ0cmFuc2Zvcm0iIHR5cGU9InJvdGF0ZSIgZnJvbT0iMCAyMCAyMCIgdG89IjM2MCAyMCAyMCIgZHVyPSIwLjVzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSIvPgo8L3BhdGg+Cjwvc3ZnPg==";

    object.setAttribute("load","loading");

    Imagess(object.getAttribute("data"), object.id, checkimg);
}




//判断是否加载完成
function Imagess(url, imgid, callback) {
    console.log("开始加载" + url);
    var img = new Image();
    var val=url;
    var load;
    img.onload = function () {
        if (img.complete === true) {
            callback(img, imgid);
        }
    };

    //如果因为网络或图片的原因发生异常，则显示该图片
    img.onerror = function (event) {
        document.getElementById(imgid).style.cssText ="margin: 10px 100px;max-width: 40%!important;";
        load = false;
        document.getElementById(imgid).setAttribute("load","fail");
        img.src = "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgNzguMTI0OTk0MDM5NTM1NTIgNTAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHZlcnNpb249IjEuMSIgc3R5bGU9IndpZHRoOiBhdXRvO2hlaWdodDogYXV0bzsvKiBiYWNrZ3JvdW5kOnJnYig3OCwgNzgsIDc4KSAqLyI+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCAwKSBzY2FsZSgwLjc4MTI1KSIgaWQ9ImxvY2t1cDQtaWNvbiI+PGcgZmlsbD0iIzRlNGU0ZSI+PHBhdGggZD0iTTAgOC4zOTJ2NDcuMjE2QzAgNjAuMjQzIDMuNzEgNjQgOC4yOTUgNjRINTguMzhjNC41ODEgMCA4LjI5NS0zLjc2IDguMjk1LTguMzkyVjguMzkyQzY2LjY3NSAzLjc1NyA2Mi45NjUgMCA1OC4zOCAwSDguMjk1QzMuNzE0IDAgMCAzLjc2IDAgOC4zOTJ6bTQ1LjM5NCAxNC45MWMtMS45OTggMi4wNDctNS44NyAyLjA0Ny01Ljg3IDIuMDQ3YTUuNTAyIDUuNTAyIDAgMCAwIDQuNjQzIDIuNTZjMy4wNSAwIDUuNTItMi40OTggNS41Mi01LjU4MSAwLTEuOTAyLS45NC0zLjU4MS0yLjM3Ni00LjU4OSAwIDAgLjA4MSAzLjUxNi0xLjkxNyA1LjU2M3pNNDEuMTY4IDU4TDIzLjYzMiAzMC45MTggNSA0Ny4zOTVWNy44MTlDNSA2LjI2MyA2LjI1MiA1IDcuNzkzIDVoNTAuNDE0QzU5Ljc1MiA1IDYxIDYuMjYgNjEgNy44MnY0NS4xNjdMNDYuNjggMzguNTk2bC0xMS4xMTUgNS42TDQ0LjYyOCA1OGgtMy40NnptLTIxLjc5NS0xLjkwM2MuODI5IDAgMi4wOTctLjM2NiAyLjA5Ny0xLjQ3MiAwLTEuMTA3LTEuMjY4LTIuNTM1LTIuMDk3LTIuNTM1LS44MjggMC0xLjUuODk3LTEuNSAyLjAwNCAwIDEuMTA2LjY3MiAyLjAwMyAxLjUgMi4wMDN6bTkuNTgzLTkuMzVoLTQuOTUydjEuMzM2aDUuMzMzdi0xLjMzNWgtLjM4em0tMTIuMDAxIDBoLTQuOTUzdjEuMzM2aDUuMzMzdi0xLjMzNWgtLjM4ek04MS4zMzYgOC42ODNjLjA2MiAwLTUuODM4IDYuOTE4LTUuODM4IDYuOTE4bC0uOTEyIDEuMDk2aDguMDkxVjE1LjM2aC01LjI0Nmw1Ljc1OS02LjkxOS45MS0xLjA5NWgtOC4wOXYxLjMzNmg1LjMyNnptMTAuMDc2LTMuMDUzbC00LjE3MyA0Ljk0Ni0uNjUxLjc4M0g5Mi4zN3YtLjk1NWgtMy44MzZsNC4yMDItNC45NDUuNjUyLS43ODNoLTUuNzg0di45NTRoMy44MDd6bTcuNDI0LTEuNzE3bC0yLjUyMyAyLjk2NS0uMzkuNDdoMy40Njd2LS41NzNoLTIuMjU4bDIuNDc3LTIuOTY1LjM5MS0uNDdoLTMuNDY4di41NzNoMi4zMDR6IiBmaWxsPSIjNGU0ZTRlIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiLz48L2c+PC9nPjwvc3ZnPg==";
    };
    img.src = val;
}

//显示图片
function checkimg(obj, imgid) {
    status = document.getElementById(imgid).getAttribute("load");
    if (status!=="fail"){
        document.getElementById(imgid).setAttribute("load","success");
    }
    document.getElementById(imgid).src = obj.src;
}

function showPage() {

    imagesLoad();
    clickLink();
    clickImage();


}
