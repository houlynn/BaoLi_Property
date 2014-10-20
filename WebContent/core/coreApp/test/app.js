Ext.onReady(function(){
	Ext.application({
		name:"core",//引用的名称
		scope :this,
        appFolder : "core/coreApp",//应用的目录
        autoCreateViewport: 'core.test.view.Main'  
	})
});