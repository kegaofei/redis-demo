function getArticlesByScore(){
	 $('#articleShowBody').html("");
	$.ajax({
		url: '/getArticlesByScore',  
		type: 'GET',
		cache: false,
		success: function(data) {
				var strTd = "";
				var strTh = "";
                for (i in data) {
                    strTd += "<tr>" +
                        "<td align='center'>" + data[i].id + "</td>" +
                        "<td align='center'>" + data[i].title + "</td>" +
                        "<td align='center'>" + data[i].link + "</td>" +
                        "<td align='center'>" + data[i].user + "</td>" +
                        "<td align='center'>" + data[i].now + "</td>" +
                        "<td align='center'><a href='#'>" + data[i].votes + "</a></td>" +
                        "</tr>";
                }
                strTh = "<tr>" +
				"<th width='500'>article id</th>" +
				"<th width='500'>title</th>	" +	
				"<th width='1000'>link</th>" +
				"<th width='500'>poster</th>" +
				"<th width='500'>time</th>" +
				"<th>votes</th>" +
			"</tr>";
                $('#articleShowBody').append(strTh + strTd);
		}
	});
}	
function getArticlesByTime(){
	$('#articleShowBody').html("");
	$.ajax({
		url: '/getArticlesByTime',  
		type: 'GET',
		cache: false,
		success: function(data) {
			var strTd = "";
			var strTh = "";
			for (i in data) {
				strTd += "<tr>" +
				"<td align='center'>" + data[i].id + "</td>" +
				"<td align='center'>" + data[i].title + "</td>" +
				"<td align='center'>" + data[i].link + "</td>" +
				"<td align='center'>" + data[i].user + "</td>" +
				"<td align='center'>" + data[i].now + "</td>" +
				"<td align='center'><a href='#'>" + data[i].votes + "</a></td>" +
				"</tr>";
			}
			strTh = "<tr>" +
			"<th width='500'>id</th>" +
			"<th width='500'>title</th>	" +	
			"<th width='1000'>link</th>" +
			"<th width='500'>poster</th>" +
			"<th width='500'>time</th>" +
			"<th>votes</th>" +
			"</tr>";
			$('#articleShowBody').append(strTh + strTd);
		}
	});
}	

function addArticle(){
	$.ajax({
		url: '/addArticle',  
		data: {
			title: $("#addTitle").val(),
			user: $("#addUser").val(),
			link: $("#addLink").val()
		},
		type: 'POST',
		cache: false,
		success: function(data) {
		}
	});
}

function articleVote(){
	$.ajax({
		url: '/articleVote',  
		data: {
			id: $("#voteId").val(),
			user: $("#voteUser").val()
		},
		type: 'POST',
		cache: false,
		success: function(data) {
		}
	});
}

function cancelArticleVote(){
	$.ajax({
		url: '/cancelArticleVote',  
		data: {
			id: $("#cancelVoteId").val(),
			user: $("#cancelVoteUser").val()
		},
		type: 'POST',
		cache: false,
		success: function(data) {
		}
	});
}

getArticlesByScore();

