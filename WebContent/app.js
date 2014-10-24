Ext.onReady(function(){
	Ext.application({
		name:"app",//引用的名称
		scope :this,
        appFolder : "",//应用的目录
       autoCreateViewport: 'app.view.main.Main' , 
	})
});


