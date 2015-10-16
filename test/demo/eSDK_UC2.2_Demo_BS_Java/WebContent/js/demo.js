function changeLang()
{
	// 登录页面传入的值
	lang = $("#changeLang").val();
	
	if (lang == "en-us")
	{
		controller = new LanguageControllerEN();
	}
	else
	{
		controller = new LanguageControllerZH();
	}
	
	var langTags = $("[languageTag]");
	for(var i = 0; i < langTags.size(); i++)
	{
		var langTag = langTags.eq(i);
		var tag = langTag.attr('languageTag');
		langTag.html(controller.language.titles[tag]);
		langTag.val(controller.language.titles[tag]);

	}
	
	var zTree = $.fn.zTree.getZTreeObj("eSDKTree");
	var nodes = zTree.getNodes();
	changeTreeName(nodes[0], zTree, controller);
	
}

function changeTreeName(treeNode, zTree, controller)
{
	treeNode.name=controller.language.tree[treeNode.languageTag];
	zTree.updateNode(treeNode);
	
	if (treeNode.isParent)
	{
		for(var obj in treeNode.children)
		{
			changeTreeName(treeNode.children[obj], zTree, controller);
		}
    }

	return;
}

function appendInterface(pName)
{
	if ("createCTD" == pName)
	{
		$(function() {
			$.ajax({
				type : "get",
				cache : false,
				url : "pages/interface/createctd.html",
				async : false,
				success : function(data) {
					$("#main1").html(data);
				}
			});
		});
	}
	
	if ("addAccount" == pName)
	{
		$(function() {
			$.ajax({
				type : "get",
				cache : false,
				url : "pages/interface/addAccount.html",
				async : false,
				success : function(data) {
					$("#main1").html(data);
				}
			});
		});
	}
	
	if ("queryAccount" == pName)
	{
		$(function() {
			$.ajax({
				type : "get",
				cache : false,
				url : "pages/interface/queryAccount.html",
				async : false,
				success : function(data) {
					$("#main1").html(data);
				}
			});
		});
	}
	
    setTab(1,0);
	
	changeLang();
}

function setTab(m,n)
{  
	 var tli=document.getElementById("menu"+m).getElementsByTagName("li");  
	 var mli=document.getElementById("main"+m).getElementsByTagName("ul");  
	 for(i=0;i<tli.length;i++)
	 {  
	 	tli[i].className=i==n?"hover":"";  
	  	mli[i].style.display=i==n?"block":"none";  
	 }  
}

function clean()
{
	// 当单击节点时，置空标签页
	$("#main1").html("<ul class='block'><li></li></ul><ul><li></li></ul><ul><li></li></ul>");
	setTab(1,0);
}

function isEmpty(value)
{
	if (null == value || "" == value)
	{
		return true;
	}
	else
	{
		return false;
	}
}

function isNumber(num)
{
	var reNum=/^\d*$/;
	return(reNum.test(num));
}
