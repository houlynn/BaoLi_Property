/**
 * 折叠式(accordion)菜单，样式可以自己用css进行美化
 */

Ext.define('core.app.view.main.menu.AccordionMainMenu', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.mainmenuaccordion',
			title : '系统菜单',
			requires:['core.app.view.main.MainModel','core.app.view.main.MainController'],
			layout : {
				type : 'accordion',
				animate : true
			},
			initComponent : function() {
				alert(0);
				this.items = [];
				var vm = this.up('app-main').getViewModel();
				console.log("打印console=======================");
				console.log(vm)
				var menus = vm.get('tf_MenuGroups');
				var me = this;
				for (var i in menus) {
					var menugroup = menus[i];
					var accpanel = {
						menuAccordion : true,
						xtype : 'panel',
						title : menugroup.tf_title,
						bodyStyle : {
							padding : '10px'
						},
						layout : 'fit',
						dockedItems : [{
									dock : 'left',
									xtype : 'toolbar',
									items : []
								}],
						glyph : menugroup.tf_glyph
					};
					for (var j in menugroup.tf_menuModules) {
						var menumodule = menugroup.tf_menuModules[j];
						var module = vm.getModuleDefine(menumodule.tf_ModuleId);
						if (module) {
							console.log("===============开发的===============");
							alert(module.tf_moduleName)
							accpanel.dockedItems[0].items.push({
										xtype : 'buttontransparent',
										text : this.addSpace(module.tf_title, 12),
										glyph : module.tf_glyph,
										handler : 'onMainMenuClick'
									});
						}
					}
					this.items.push(accpanel);
				}
				this.callParent(arguments);
			},

			addSpace : function(text, len) {
				var result = text;
				for (var i = text.length; i < len; i++) {
					result += '　';
				}
				return result;
			}

		})