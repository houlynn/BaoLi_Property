Ext.onReady(function(){
	Ext.application({
		name:"core",//引用的名称
		scope :this,
        appFolder : "core1/coreApp",//应用的目录
        autoCreateViewport: 'core.app.view.main.Main' , 
	})
});


