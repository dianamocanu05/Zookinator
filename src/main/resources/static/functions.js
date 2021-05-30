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
    success: function(data) { alert("success",data); },
    error: function(data) {alert("fail",data); },
     dataType: 'json'
  })
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
