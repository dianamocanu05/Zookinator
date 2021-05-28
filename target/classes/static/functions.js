function post(){
responses = getResponses();
object = mapToJson(responses);
$.ajax({
    type: 'POST',
    url : "http://localhost:5432/api/send",
    contentType: "application/json",
     headers: {
        "Content-type": "application/json",
        "Access-Control-Allow-Origin": "*"
      },
    data: JSON.stringify(object),
    // success: function(data) { alert("ajax worked"); },
    // error: function(data) {alert("ajax error"); },
     dataType: 'json'
  })
}

function getResponses(){
    var checkboxes = document.getElementsByName("input");
    var map = new Map();
    var tags = [hasHair,hasFeathers,laysEggs,hasMilk,isAirborne,isAquatic,isPredator,isToothed,hasBackbone,breathes,isVenomous,hasFins,legs,hasTail,isDomestic,isCatsized];
    for(var i = 0; i < checkboxes.length; i++) {
            map.put(tags[i], checkboxes[i]);
    }
    return map;
}

function mapToJson(Map map){
    object = {};
    map.forEach((value, key) => {
        var keys = key.split('.'),
            last = keys.pop();
        keys.reduce((r, a) => r[a] = r[a] || {}, object)[last] = value;
    });
    return object;
}



