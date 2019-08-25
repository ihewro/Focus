window.addEventListener("load", showPage);

function showPage() {
    clickImage();

};

function clickImage() {


    var objs = document.getElementsByTagName("img");
    for (var i = 0; i < objs.length; i++) {
        touchImage(objs[i]);
        console.log("okle!");
    }
};


function touchImage(btn) {
    btn.addEventListener("click", SinglePress, false);

    function SinglePress() {
        console.log("单击");
        window.imagelistener.longClickImage(btn.src);

    }
}