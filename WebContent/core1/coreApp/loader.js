/**
 * 程序外部组件引用
 */
 //初始化Ext.QuickTips，以使得tip提示可用
 Ext.QuickTips.init();
//初始化提示工具框
Ext.tip.QuickTipManager.init();
//启动动态加载机制
Ext.Loader.setConfig({
	enabled:true,
	paths:{
		baseUx:"core1/coreApp/ux/",
	}
})
///同步加载
Ext.syncRequire([
        "baseUx.Ext.ux.BoxReorderer",
        "baseUx.Ext.ux.TabReorderer",
       
]);
