function showMsg(title ,msg,type){
		if(type==1){
			Ext.toast({
				title : title,
				html : title,
				bodyStyle : 'background-color:#7bbfea;',
				header : {
					border : 1,
					style : {
						borderColor : '#9b95c9'
					}
				},
				border : true,
				style : {
					borderColor : '#9b95c9'
				},
				saveDelay : 10,
				align : 'tr',
				closable : true,
				minWidth : 200,
				useXAxis : true,
				slideInDuration : 500
			});			
		}else if(type==1){
			Ext.toast({
				title : title,
				html : msg,
				bodyStyle : 'background-color:green;',
				header : {
					border : 1,
					style : {
						borderColor : 'pink'
					}
				},
				border : true,
				style : {
					borderColor : 'pink'
				},
				saveDelay : 10,
				align : 'tr',
				closable : true,
				minWidth : 200,
				useXAxis : true,
				slideInDuration : 500
			});	
			
		}else if(type=0){
			Ext.MessageBox.show({
				title : title,
				msg : msg,
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
			
		}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
}