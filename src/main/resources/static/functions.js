function post(){
object = {}
object = getResponses();
console.log(JSON.stringify(object));
$.ajax({
    type: 'POST',
    url : "http://localhost:5432/api/send",
    contentType: "application/json",
     headers: {
        "Content-type": "application/json",
        "Access-Control-Allow-Origin": "*"
      },
    data: JSON.stringify(object),
    success: function(data) { successCallback(data) },
    error: function(data) {failCallback(data) },
     dataType: 'json'
  })
}

//function postImage(){
//    $('#form').submit(function(){
//         console.log('HERE');
//        $.ajax({
//          url: $('#form').attr('action'),
//          type: 'POST',
//          data : $('#form').serialize(),
//          success: function(data){
//            successCallback(data);
//          }
//        });
//        return false;
//    });
//
//}


function successCallback(responseObj){
    alert("You thought about a " + responseObj.prediction)
}
function getResponses(){

        var checkboxes = document.getElementsByName("input");
        var values = [];
         var tags = ["hasHair","hasFeathers","laysEggs","hasMilk","isAirborne","isAquatic","isPredator","isToothed","hasBackbone","breathes","isVenomous","hasFins","legs","hasTail","isDomestic","isCatsized"];
        for(var i = 0; i < checkboxes.length; i++) {
            if(checkboxes[i].checked){
            values.push(checkboxes[i].value);
            }
        }
        for(val in values){
            console.log(values[val]);
        }

        var object = {};
        tags.forEach(function(k,i) {
            object[k] = values[i];
        })
        return object;

}

//window.onload = function(){
//    document.forms["fileUploadForm"].onsubmit = async(e) => {
//      e.preventDefault();
//      const params = new URLSearchParams(new FormData(e.target));
//      console.log(params);
//      fetch("http://localhost:5432/api/image", {method:"POST", body:params});
//      const response = await new Response(params).text();
//      console.log(response);
//    }
//}