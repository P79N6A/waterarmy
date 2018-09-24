!function(t) {
	function e(i){if(n[i])return n[i].exports;var o=n[i]={i: i,l:!1,exports:{
}};

return t[i].call(o.exports,o,o.exports,e),o.l=!0,o.exports
}

var n= {
};

e.m=t,e.c=n,e.d=function(t,n,i) {
e.o(t,n)||Object.defineProperty(t,n,{configurable: !1,enumerable:!0,get:i
})},e.n=function(t) {
var n=t&&t.__esModule?function(){return t.default
}

:function() {
return t
};

return e.d(n,"a",n),n
},e.o=function(t,e) {
return Object.prototype.hasOwnProperty.call(t,e)
},e.p="",e(e.s=0)
}([function(t,e,n) {
"use strict"
;Object.defineProperty(e,"__esModule",{value: !0
});

var i=n(1),o=n(2),l=n(3),s=n(4),a=n(5),r=n(6),c=n(7),u=n(8),d=n(9),h=n(10),p=n(11),f=n(12),m=n(13),g=n(14),v=n(15),b=n(16),y=n(17),k=n(18),w=n(19),x=n(20),S=n(21),C=n(22),$=n(23),T=n(24),L=n(25),M=n(26),R=n(27),D=n(28);!function(t) {
t.use({install: function(t,e){[i.a,o.a,l.a,s.a,a.a,c.a,u.a,d.a,h.a,r.a,p.a,f.a,m.a,g.a,v.a,b.a,y.a,k.a,w.a,x.a,S.a,C.a,T.a,L.a,$.a].forEach(function(e){t.component(e.name,e)
}),t.prototype.$bus=D.a,t.mixin(R.a)
}});

var e=t.extend(M.a);window.App=e
}(Vue)
},function(t,e,n) {
"use strict"
;e.a={name: "index",props:["topMenus"],data:function(){return{toggle:!1,activeIndex:["0"],defaultActive:"0",showMenu:!0,activeTab:"",tabs:[],menus:[],isMouseOver:!1
}},watch: {
activeTab: function(t){console.log(t),this.session("activeTab",t),this.tabs&&this.tabs.forEach(function(e){e.url==t&&(e.inited=!0)
});

var e=window[t];e&&e.app&&this.$nextTick(function() {
e.app.scrollBack&&e.app.scrollBack()
})},tabs:function(t,e) {
var n=this;t&&t.forEach(function(t){void 0==t.inited&&n.$set(t,"inited",!1)
}),this.session("tabs",t)
},menus:function(t) {
this.session("menus",t)
},toggle:function(t) {
this.session("toggle",JSON.stringify(t))
}},methods: {
onMouseLeave: function(){this.isMouseOver=!1
},onMouseOver:function() {
this.isMouseOver=!0
},refresh:function() {
sessionStorage.clear(),location.reload()
},onSelect:function(t,e) {
var n=this,i=Number(e[0]),o=this.topMenus[i];if("_blank"==o.target)return void window.open(o.url);if("_top"==o.target)return void this.addTab({title: o.showTitle,url:o.url,target:o.target
});

if(o.language) {
var l=Number(t.split("-")[1]),s=o.child[l].lang;return void(localStorage.activeLang!=s&&(localStorage.activeLang=s,location.reload()))
}

this.activeIndex=["0"],this.showMenu=!1,o.subMenus&&(this.menus=o.subMenus,this.tabs=[this.menus[0].child[0]],this.tabs[0].inited=!0,this.activeTab=this.tabs[0].url,this.defaultActive="0",setTimeout(function() {
n.showMenu=!0
},0)),o.logOut&&sessionStorage.clear(),o.url&&(location.href=o.url)
},onSelectMenu:function(t,e) {
if(this.menus[e[0]].child[e[1]].subchild)var n=t.split("-")[1],i=this.menus[e[0]].child[e[1]].subchild[n];else var i=this.menus[e[0]].child[e[1]];this.addTab(i)
},changeTab:function(t,e) {
},removeTab:function(t) {
var e=this,n=this.tabs;this.activeTab==t&&n.forEach(function(i,o){if(i.url==t){var l=n[o+1]||n[o-1];l&&(e.activeTab=l.url)
}}),this.tabs=this.tabs.filter(function(e) {
return e.url!=t
})},addTab:function(t) {
var e=!1;this.tabs.forEach(function(n){n.url==t.url&&(e=!0),"_self"==t.target&&t.url.substr(0,t.url.indexOf("?"))==n.url.substr(0,n.url.indexOf("?"))&&(e=!0,n.url=t.url)
}),e||this.tabs.push(t),this.activeTab=t.url
}},created:function() {
var t=this;this.$bus.on("addTab",function(e){t.addTab(e)
}),this.$bus.on("closeTab",function() {
t.removeTab(t.activeTab)
}),this.topMenus&&this.topMenus.forEach(function(e) {
e.subMenus&&e.subMenus.forEach(function(e){e.badge||t.$set(e,"badge",0)
})}),this.toggle=this.session("toggle")||!1,this.menus=this.topMenus[0].subMenus,this.tabs=this.session("tabs")||[],this.tabs.forEach(function(e) {
t.$set(e,"inited",!1)
}),this.activeTab=this.session("activeTab")||"",this.menus&&this.menus.length&&this.menus[0].child&&this.menus[0].child.length&&!this.tabs.length&&(this.tabs=[this.menus[0].child[0]],this.tabs.forEach(function(e) {
t.$set(e,"inited",!1)
}),this.activeTab=this.tabs[0].url)
},mounted:function() {
var t=this.getQuery("goPage");t&&this.addTab({url: decodeURIComponent(t),title:"页面"
})}}},function(t,e,n) {
"use strict"
;e.a={name: "index-modal",template:'<transition><div class="v-modal" style="pointer-events:none;" v-show="modalShow"> </div></transition>',data:function(){return{modalShow:!1
}},created:function() {
var t=this;this.$bus.on("modalToggle",function(e){t.modalShow=e
})}}},function(t,e,n) {
"use strict"
;e.a={name: "tree",template:'\n    <div class="ui-root-wrap" :class="{\'ui-root-button\':skin == \'button\'}">\n\n    <template v-if="skin == \'button\'">\n    <el-row type="flex" :gutter="5">\n      <el-col><el-input placeholder="请选择" v-model="checkedNodesString" readonly class="input" :disabled="disabled"></el-input></el-col>\n      <el-col style="width:70px;"><el-button @click="onOpenRoot" :type="btnType" :size="btnSize">{{btnText}}</el-button></el-col>\n    </el-row>\n    </template>\n\n    <template v-else-if="skin == \'noinput\'">\n    <el-button @click="onOpenRoot" :type="btnType" :size="btnSize" :disabled="disabled">{{btnText}}</el-button>\n    </template>\n\n    <template v-else-if="skin == \'plus\'">\n    <div class="ui-root-plus">\n    <el-row :gutter="5">\n    <el-col :span="17">\n    <el-input placeholder="输入关键字进行搜索" :icon="isRootSearch ? \'close\': \'search\'" :on-icon-click="onSearch" v-model="keyword" @change="onKeywordChange"></el-input>\n    </el-col>\n    <el-col :span="7">\n    <el-button @click="clearChecked">清空</el-button>\n    </el-col>\n    </el-row>\n\n    <el-tree :props="props" :load="getNode" node-key="id" highlight-current lazy :show-checkbox="!isSingle" :check-strictly="checkStrictly" ref="root" v-show="!isRootSearch" v-loading="loading" @current-change="currentChange" :expand-on-click-node="true" @node-click="nodeClick"> </el-tree>\n\n    <el-tree :data="roots" :props="props" node-key="id" highlight-current :show-checkbox="!isSingle" :check-strictly="checkStrictly" ref="rootSearch" v-show="isRootSearch" @current-change="currentChange" :expand-on-click-node="false" @node-click="nodeClick"> </el-tree>\n    </div>\n    </template>\n\n    <template v-else>\n    <el-input placeholder="请选择" icon="search" @click="onOpenRoot" @focus="onOpenRoot" v-model="checkedNodesString" readonly :disabled="disabled"></el-input>\n    </template>\n\n    <el-dialog :title="title" :visible.sync="isShowModal" :size="dialogSize" class="ui-root" :close-on-click-modal="false" @close="modalClose" @open="onModalOpen" top="0%" :modal="locModal">\n\n      <el-row>\n        <el-col :span="10" v-if="thisList && thisList.length">\n        <div class="ui-root-list-top"><el-button size="small" @click="clearAll">清空所选</el-button></div>\n        <ul class="ui-root-list">\n        <li v-for="(item,index) in thisList"><i class="el-icon-close" @click="removeItem(item,index)"></i>{{item.name}}</li>\n        </ul>\n        </el-col>\n        <el-col :span="1" v-if="thisList && thisList.length"></el-col>\n        <el-col :span="(thisList && thisList.length) ? 13 : 24">\n\n        <template v-if="data && data.length">\n        <div class="ui-root-search">\n          <input class="el-input__inner" v-model="keyword" placeholder="请输入关键字回车搜索" @keyup.enter="doFilter">\n          <i :class="{\'el-icon-circle-cross\': keyword,\'el-icon-search\':!keyword}" @click="clearFilter"></i>\n        </div>\n\n        <el-tree :props="props" :data="data" :node-key="value" highlight-current  :show-checkbox="!isSingle" :check-strictly="true" ref="root" v-show="!isRootSearch" v-loading="loading" @current-change="currentChange" :expand-on-click-node="true" @node-click="nodeClick" :filter-node-method="filterNode" :default-checked-keys="defaultCheckedIds"> </el-tree>\n        </template>\n\n        <template v-else>\n\n        <el-row :gutter="5">\n        <el-col :span="20">\n        <div class="ui-root-search">\n        <input class="el-input__inner" v-model="keyword" placeholder="请输入关键字回车搜索" @keyup.enter="onSearch">\n        <i :class="{\'el-icon-circle-cross\': keyword,\'el-icon-search\':!keyword}" @click="clearSearch"></i>\n        </div>\n        </el-col>\n        <el-col :span="3">\n        <el-button @click="clearChecked">清空所选</el-button>\n        </el-col>\n        </el-row>\n\n        <el-tree :props="props" :load="getNode" node-key="id" highlight-current lazy :show-checkbox="!isSingle" :check-strictly="checkStrictly" ref="root" v-show="!isRootSearch" v-loading="loading" @current-change="currentChange" :expand-on-click-node="true" @node-click="nodeClick"> </el-tree>\n\n        <el-tree :data="roots" :props="props" node-key="id" highlight-current :show-checkbox="!isSingle" :check-strictly="checkStrictly" ref="rootSearch" v-show="isRootSearch" v-loading="loading" @current-change="currentChange" :expand-on-click-node="true" @node-click="nodeClick" :default-expand-all="true"> </el-tree>\n        </template>\n        \n        </el-col>\n      </el-row>\n\n      <span slot="footer" class="dialog-footer">\n        <el-button @click="isShowModal = false">取 消</el-button>\n        <el-button type="primary" @click="getCheckedNodes">确 定</el-button>\n      </span>\n    </el-dialog>\n\n    </div>\n    ',props:{checkedNodes:null,checkedIds:null,skin:null,checkedList:null,urls:null,modal:{default:!0
},isSingle: {
default: !1
},label: {
default: "name"
},value: {
default: "id"
},title: {
default: "问题分类"
},enableParent: {
default: !0
},btnText: {
default: "选择"
},btnType: {
default: ""
},btnSize: {
default: ""
},disabled: {
default: function(){return!1
}},checkStrictly: {
default: function(){return!1
}},data:Array
},computed: {
dialogSize: function(){return this.modal?this.thisList&&this.thisList.length?"large":"small":"large"
},odata:function() {
var t=this;if(this.data&&this.data.length){var e={
},n=function(i) {
i.forEach(function(i){e[i[t.value]]=i,i[t.props.children]&&i[t.props.children].length&&n(i[t.props.children])
})};

return n(this.data),e
}}},data:function() {
return{isShowModal: !1,isRootSearch:!1,checkedNodesString:"",defaultCheckedIds:[],roots:[],props:{label:this.label,children:"children"
},keyword:"",loading:!1,thisList:[],init:!1,locUrls:this.urls|| {
root: "/system/basics/problemCategory/queryRootByAccount.do",leaf:"/system/basics/problemCategory/queryLeafByAccount?parent=",search:"/system/basics/problemCategory/searchByAccount.do?name="
},locModal:this.modal,selectNode: {
},timer: {
},dbTimer:null,dbItem:"",dbTime:null,isDb:!1
}},watch: {
checkedList: function(t){t&&(this.thisList=t,this.checkedNodesString=t.map(function(t){return t.name
}).join(","))
},checkedIds: {
handler: function(t,e){var n=this;
if(this.data){var i=function(t){if(n.odata[t]){return n.odata[t][n.label]
}

return null
};

if(t) {
this.defaultCheckedIds=t.split(",");var o=this.defaultCheckedIds.map(function(t){return i(t)
}).join(",");this.checkedNodesString=o
}}},immediate:!0
}},methods: {
clearFilter: function(){this.$refs.root.filter(""),this.keyword=""
},doFilter:function() {
this.$refs.root.filter(this.keyword)
},filterNode:function(t,e) {
return!t||(console.log("test"),console.log(e),console.log(this.label),console.log(e[this.label]),-1!==e[this.label].indexOf(t))
},nodeClick:function(t,e,n) {
var i,o,l=(new Date).getTime();this.dbTime&&l-this.dbTime<500&&(this.checkedNodesString=this.selectNode[this.label],i=this.selectNode[this.value],o=this.selectNode,this.$emit("update:checkedIds",i),this.$emit("confirm",o),this.isShowModal=!1),this.dbTime=l
},clearChecked:function() {
this.$refs.root.setCheckedKeys([]),this.$refs.rootSearch.setCheckedKeys([])
},clearAll:function() {
this.thisList=[]
},removeItem:function(t,e) {
this.thisList.splice(e,1)
},getNode:function(t,e) {
var n=this;this.loading=!0;var i=0===t.level?this.locUrls.root: this.locUrls.leaf+t.data[this.value];
console.log("value",this.value),this.get(i,function(t){e(t.data),n.loading=!1
})},getCheckedNodes:function() {
var t,e,n=this;if(this.isSingle)this.checkedNodesString=this.selectNode[this.label],t=this.selectNode[this.value],e=this.selectNode;else{var i=this.isRootSearch?"rootSearch": "root",e=this.$refs[i].getCheckedNodes();
this.thisList.length&&(e=e.concat(this.thisList)),this.$emit("update:checkedNodes",e),this.checkedNodesString=e.map(function(t){return t[n.label]
}).join(","),console.log("checkedNodesString",this.checkedNodesString),t=e.map(function(t) {
return t[n.value]
}).join(",")
}

if(!this.enableParent&&this.isSingle&&!e.leaf)return void this.alert("请选择具体模板");this.$emit("update:checkedIds",t),this.$emit("confirm",e),this.isShowModal=!1
},clearSearch:function() {
this.isRootSearch=!1,this.roots=[],this.keyword=""
},onSearch:function() {
var t=this;if(!this.keyword)return this.isRootSearch=!1,this.roots=[],void(this.keyword="");if(""!=this.keyword){this.loading=!0;var e=this.locUrls.search+this.keyword;this.get(e,function(e){t.roots=e.data,t.isRootSearch=!0,t.loading=!1
})}},onOpenRoot:function() {
this.isShowModal=!0
},modalClose:function() {
!0===this.locModal&&this.onModalClose()
},currentChange:function(t) {
this.selectNode=t
},onKeywordChange:function() {
var t=this;clearTimeout(this.timer),this.timer=setTimeout(function(){t.onSearch()
},500)
}},mounted:function() {
this.checkedList&&this.checkedList.length&&(this.thisList=this.checkedList,this.checkedNodesString=this.thisList.map(function(t){return t.name
}).join(","),this.init=!0)
}}},function(t,e,n) {
"use strict"
;e.a={name: "daterange",template:'<div :class="classObj"><el-date-picker v-model="date" :type="type" :placeholder="placeholder" :clearable="false" @change="onchange" :editable="false" :disabled="disabled" :picker-options="pickerOptions"> </el-date-picker></div>',props:{startDate:null,endDate:null,recent:null,format:{default:"date"
},type: {
default: "datetimerange"
},disabled: {
default: !1
},placeholder: {
default: "请选择"
},value:null,lastDays:Number,maxDays:Number,initRange:Array
},computed: {
classObj: function(){return"ui-"+this.type
}},data:function() {
return{date: this.value||"",pickerOptions:{
},isInit:!1
}},watch: {
date: function(t){if(t){if("datetimerange"==this.type){var e=t;
"timestamp"
==this.format&&(e=[t[0].getTime(),t[1].getTime()]),e[0]&&e[1]&&13==e[0].toString().length&&13==e[1].toString().length?(this.$emit("update:startDate",this.DateFormat(e[0],"yyyy-MM-dd hh:mm:ss")),this.$emit("update:endDate",this.DateFormat(e[1],"yyyy-MM-dd hh:mm:ss"))): (this.$emit("update:startDate",e[0]),this.$emit("update:endDate",e[1]))
}}

else this.$emit("update:startDate",""),this.$emit("update:endDate","")
},value: {
handler: function(t,e){this.date=e&&""==t?"":t
},immediate:!0
},startDate: {
handler: function(t,e){"datetimerange"==this.type&&this.syncDate()
},immediate:!0
}},methods: {
syncDate: function(){this.date=[this.startDate,this.endDate]
},onchange:function(t) {
if(t){if(this.$emit("input",t),"daterange"==this.type||"datetimerange"==this.type){var e=t.split(" - ");this.$emit("update:startDate",e[0]),this.$emit("update:endDate",e[1]),this.isInit||(this.$emit("init",e),this.isInit=!0)
}}

else this.$emit("input",""),this.$emit("update:startDate",""),this.$emit("update:endDate","")
},clear:function() {
this.date=""
}},mounted:function() {
var t=this;if(void 0!=this.maxDays&&this.$set(this.pickerOptions,"onPick",function(e){if(!e.maxDate&&""!=e.minDate){var n=e.minDate,i=new Date(n);i.setDate(i.getDate()+t.maxDays),t.$set(t.pickerOptions,"disabledDate",function(t){return t.getTime()>i.getTime()
})}}),this.recent) {
var e=this.recent,n=new Date;n.setHours(e,0,0);var i=new Date(n.getTime());i.setDate(n.getDate()-1),this.date=[i,n]
}

if(this.lastDays) {
var n=new Date;this.recent&&n.setHours(this.recent,0,0);var i=new Date(n.getTime());i.setDate(n.getDate()-this.lastDays),this.date=[i,n]
}

if(this.initRange&&this.initRange.length) {
var o=function(e){var n=new Date,i="min"==e?n.getDate()-t.initRange[0]: n.getDate()+t.initRange[1];
return n.setDate(i),n.getTime()
},i=o("min"),n=o("max");this.date=[i,n]
}

this.startDate&&this.endDate&&(this.date=[this.startDate,this.endDate])
}}},function(t,e,n) {
"use strict"
;e.a={name: "multiselect",template:'\n  <div class="ui-multiselect" :class="{\'ui-multiselect-button\':skin == \'button\'}">\n\n  <template v-if="skin == \'button\'">\n  <el-row type="flex" :gutter="5">\n    <el-col><el-input :placeholder="btnText" readonly class="input" :value="checkedText"></el-input></el-col>\n    <el-col style="width:70px;"><el-button @click="open">{{btnText}}</el-button></el-col>\n  </el-row>\n  </template>\n\n  <template v-else-if="skin == \'noinput\'">\n  <el-button @click="open" :type="btnType">{{btnText}}</el-button>\n  </template>\n\n  <template v-else>\n  <el-input :placeholder="btnText" icon="caret-bottom" @focus="open" @click="open" :value="checkedText" readonly :disabled="disabled"></el-input>\n  </template>\n\n  <el-dialog :title="title" :visible.sync="isShowModal" size="small" :close-on-click-modal="false" @close="modalClose" @open="onModalOpen" top="0%" :modal="locModal">\n\n    <el-row :gutter="10" type="flex">\n    <el-col :span="24"><el-input placeholder="关键字" v-model="keyword" :icon="isSearch ? \'close\':\'search\'" :on-icon-click="search" @change="onChange"></el-input></el-col>\n    <el-col :span="12" v-if="!isSingle" style="width:310px">\n    <el-button-group>\n    <el-button @click="selectAll">全选</el-button>\n    <el-button @click="reverseAll">反选</el-button>\n    <el-button @click="clearAll">清空</el-button>\n    </el-button-group>\n    </el-col>\n    </el-row>\n\n    <el-tree :props="props" :data="data" highlight-current :show-checkbox="!isSingle" check-strictly ref="multiSelect" :node-key="value" :filter-node-method="filterNode" @current-change="currentChange"> </el-tree>\n\n    <span slot="footer" class="dialog-footer">\n      <el-button @click="isShowModal = false">取 消</el-button>\n      <el-button type="primary" @click="getCheckedNodes">确 定</el-button>\n    </span>\n  </el-dialog>\n  </div>\n  ',props:{data:Array,selectIds:null,selectText:null,label:null,value:{default:"id"
},onClick:null,skin:null,disabled: {
default: function(){return!1
}},checkedList:Array,isSingle: {
default: function(){return!1
}},modal: {
default: function(){return!0
}},btnText: {
default: "选择"
},btnType: {
default: ""
},title: {
default: "请选择"
}},data:function() {
return{checkedText: "",props:{label:this.label
},isShowModal:!1,keyword:"",isSearch:!1,init:!1,locModal:this.modal,selectNode: {
},isClear:!1
}},watch: {
selectText: function(t){t&&(this.checkedText=t)
},checkedList:function(t) {
var e=this;t&&t.length?(this.checkedText=t.map(function(t){return t[e.label]
}).join(","),this.$emit("update:selectIds",t.map(function(t) {
return t[e.value]
}).join(","))):this.checkedText=""
},data:function(t) {
this.matchChecked()
}},methods: {
open: function(){var t=this;
this.disabled||(this.isShowModal=!0,this.onClick&&this.onClick(),this.$nextTick(function(){if(t.isClear)return setTimeout(function(){t.clearAll()
},300),void(t.isClear=!1);t.checkedList&&t.checkedList.length&&t.$refs.multiSelect.setCheckedKeys(t.checkedList.map(function(e) {
return e[t.value]
})),t.matchChecked()
}))},getCheckedNodes:function() {
var t=this;this.isShowModal=!1;var e,n;this.isSingle?(this.checkedText=this.selectNode[this.label],e=this.selectNode[this.value],n=this.selectNode): (n=this.$refs.multiSelect.getCheckedNodes(),this.checkedText=n.map(function(e){return e[t.label]
}).join(","),e=n.map(function(e) {
return e[t.value]
}).join(",")),this.$emit("update:selectIds",e),this.$emit("update:selectText",this.checkedText),this.$emit("confirm",n)
},selectAll:function() {
var t=this;this.$refs.multiSelect.setCheckedKeys(this.data.map(function(e){return e[t.value]
}))},reverseAll:function() {
var t=this,e={
};

this.$refs.multiSelect.getCheckedKeys().forEach(function(t) {
e[t]=t
}),this.$refs.multiSelect.setCheckedKeys(this.data.filter(function(n) {
return!e[n[t.value]]
}).map(function(e) {
return e[t.value]
}))},clearAll:function() {
this.checkedText="",this.$emit("update:selectIds",""),this.$emit("update:selectText",""),this.$refs.multiSelect.setCheckedKeys([])
},setClearAll:function() {
this.isClear=!0,this.checkedText="",this.$refs.multiSelect.setCheckedKeys([])
},filterNode:function(t,e,n) {
return!t||-1!==e[this.label].indexOf(t)
},search:function() {
this.isSearch?(this.isSearch=!1,this.keyword="",this.$refs.multiSelect.filter()): this.keyword&&(this.isSearch=!0,this.$refs.multiSelect.filter(this.keyword))
},onChange:function() {
this.isSearch=!!this.keyword,this.$refs.multiSelect.filter(this.keyword)
},currentChange:function(t) {
this.selectNode=t
},modalClose:function() {
!0===this.locModal&&this.onModalClose()
},matchChecked:function() {
var t=this;if(this.selectIds){var e=this.$refs.multiSelect,n=this.selectIds.toString().split(",");this.selectIds&&this.data&&this.data.length&&e&&e.setCheckedKeys(n),this.checkedText=this.data.filter(function(e){return n.includes(e[t.value]+"")
}).map(function(e) {
return e[t.label]
}).join(",")
}}},mounted:function() {
}}},function(t,e,n) {
"use strict"
;e.a={name: "tableselect",template:'\n    <div class="ui-tableselect" :class="{\'ui-tableselect-button\':skin == \'button\'}">\n    <template v-if="skin == \'button\'">\n    <el-row type="flex" :gutter="5">\n      <el-col><el-input :placeholder="$t(\'select\',\'请选择\')" :value="checkedText" readonly class="input"></el-input></el-col>\n      <el-col style="width:90px;"><el-button @click="open">{{btnText || $t(\'select\',\'请选择\')}}</el-button></el-col>\n    </el-row>\n    </template>\n\n    <template v-else>\n    <el-input :placeholder="$t(\'select\',\'请选择\')" :icon="icon" @focus="open" @click="onIconClick" v-model="checkedText" readonly :disabled="disabled"></el-input>\n    </template>\n\n    <el-dialog :title="$t(\'select\',\'请选择\')" :visible.sync="isShowModal" size="small" :close-on-click-modal="false" @close="onModalClose" @open="onModalOpen" top="0%" :modal="modal">\n\n    <el-row class="ui-margin-bottom">\n    <el-col :span="24"><el-input :placeholder="$t(\'keyword\',\'关键字\')" v-model="keyword" :icon="isSearch ? \'close\':\'search\'" :on-icon-click="search" ></el-input></el-col>\n    </el-row>\n\n    <el-table :data="tableData" :height="height" highlight-current-row @current-change="handleCurrentChange" v-loading="loading" :empty-text="emptyText || $t(\'noData\',\'暂无数据\')">\n    <el-table-column :prop="prop.value" :label="$t(\'number\',\'编号\')" width="100"> </el-table-column>\n    <el-table-column :prop="prop.label" :label="$t(\'name\',\'名称\')"> </el-table-column>\n    </el-table>\n\n    <span slot="footer" class="dialog-footer">\n      <el-button @click="isShowModal = false">{{$t(\'cancel\',\'取消\')}}</el-button>\n      <el-button type="primary" @click="getCheckedNodes">{{$t(\'determine\',\'确 定\')}}</el-button>\n    </span>\n\n    </el-dialog>\n\n    </div>\n  ',props:{skin:{default:"default"
},disabled: {
default: !1
},modal: {
default: !0
},url: {
default: "/root?key="
},emptyText: {
default: ""
},btnText: {
default: ""
},prop: {
default: function(){return{label:"name",value:"id"
}}},checkedId:null,height: {
default: "250"
},auto: {
default: function(){return!1
}},checkedObject:null,checkedList:null
},data:function() {
return{checkedText: "",isShowModal:!1,keyword:"",isSearch:!1,tableData:[],selectNode:{
},loading:!1
}},watch: {
checkedList: {handler:function(t){t&&(this.checkedText=t[this.prop.label],this.$emit("update:checkedId",t[this.prop.value]),this.$emit("update:checkedObject",t))
},immediate:!0
}},computed: {
icon: function(){return this.checkedText?"circle-cross":"caret-bottom"
}},methods: {
open: function(){this.isShowModal=!0
},onIconClick:function() {
if(this.checkedText)return this.checkedText="",this.selectNode={
},this.$emit("update:checkedId",""),void this.$emit("update:checkedObject", {
});

this.open()
},search:function() {
var t=this;(this.auto||this.keyword)&&(this.loading=!0,this.get(this.url+this.keyword,function(e){t.loading=!1,t.tableData=e.data
}))},getCheckedNodes:function() {
this.checkedText=this.selectNode[this.prop.label],this.$emit("update:checkedId",this.selectNode[this.prop.value]);var t=this.checkedObject||{
};

for(var e in this.selectNode)this.selectNode.hasOwnProperty(e)&&(t[e]=this.selectNode[e]);this.$emit("update:checkedObject",t),this.$emit("confirm",t),this.isShowModal=!1
},handleCurrentChange:function(t) {
this.selectNode=t
}},mounted:function() {
this.auto&&this.search()
}}},function(t,e,n) {
"use strict"
;e.a={name: "popover",template:'\n  <div>\n  <template v-if="html.length < 15">\n    {{text}}\n  </template>\n\n  <template v-else>\n  <el-popover placement="top" width="400" trigger="hover">\n    <div v-html="html"></div>\n    <div slot="reference"> {{text}}...</div>\n  </el-popover>\n  </template>\n\n  </div>\n  ',props:["html","count"],data:function(){return{content:this.html,thisCount:this.count||100
}},computed: {
text: function(){return this.html.replace(/<br \/>/g,"").substring(0,this.thisCount)
}}}},function(t,e,n) {
"use strict"
;e.a={name: "editor",template:'\n    <div class="ui-editor">\n    <template v-if="type == \'textarea\'">\n    <el-input type="textarea" :placeholder="placeholder" :rows="10" v-model="thisContent" :readonly="readonly" :autosize="autosize"></el-input>\n    </template>\n    <template v-else>\n    <script :id="\'editorContent_\'+id" name="content" type="text/plain"> <\/script>\n    </template>\n    </div>\n  ',props:{type:{default:"editor"
},content:null,returnType: {
default: "html"
},readonly: {
default: function(){return!1
}},placeholder: {
default: "请输入"
},height: {
default: "320"
},autosize: {
default: function(){return!1
}}},data:function() {
return{thisContent: this.content||"",id:"",ue:{
}}},watch: {
thisContent: function(t){this.$emit("update:content",t)
},content:function(t) {
this.thisContent=t
},readonly:function(t) {
!0===t?this.ue.setDisabled(): this.ue.setEnabled()
}},methods: {
clear: function(){this.ue.setContent("")
},execCommand:function(t,e) {
this.ue.execCommand(t,e)
},getContent:function() {
return this.ue.getContent()
},setContent:function(t) {
this.ue.setContent(t+this.ue.getContent())
},contentFilter:function(t) {
var e=t.toLowerCase();if(e.match(/\<body[^\>]*\>/)){var n=e.match(/\<body[^\>]*\>/)[0],i=t.toLowerCase().indexOf(n),o=t.toLowerCase().indexOf("</body>");return t.substring(i+n.length,o)
}

return t
},initUploader:function() {
var t=this,e=document.createElement("input");e.type="file",e.className="fileUpload",e.title="插入图片",e.accept=".jpg,.jpeg,.png,.gif,.bmp",e.onchange=function(n){var i=n.target.files;e.value&&i.length&&t.getStsToken(function(e,n){var o=i[0],l=o.name.substr(o.name.lastIndexOf(".")),s=n.dir+n.filename+l;e.multipartUpload(s,o).then(function(e){var i=n.host+s;t.ue.execCommand("insertimage",{src: i
})})})},document.querySelector(".edui-for-fileUpload").appendChild(e)
},getStsToken:function(t) {
this.get("/system/basics/oss/getStsToken",function(e){var n=e.data,i=new OSS.Wrapper({accessKeyId: n.accessKeyId,accessKeySecret:n.accessKeySecret,stsToken:n.securityToken,bucket:n.bucket,region:n.region
});

t&&t(i,n)
})},initEditor:function() {
var t=this;UE.registerUI("fileUpload",function(t,e){return new UE.ui.Button({name: e,title:"插入图片",cssRules:"background-position: -380px 0px;"
})});

var e=UE.getEditor("editorContent_"+this.id, {
toolbars: [["bold","italic","underline","strikethrough","|","justifyleft","justifycenter","justifyright","|","link","forecolor","backcolor","removeformat","|","source","emotion"]],pasteplain:!0,initialFrameHeight:this.height,enableAutoSave:!1,saveInterval:5e6,elementPathEnabled:!1,wordCount:!1,allHtmlEnabled:!0,allowDivTransToP:!1,enableContextMenu:!1,autoClearinitialContent:!1,readonly:this.readonly,serverUrl:"",emotionLocalization:!0
});

e.ready(function() {
e.setContent(t.contentFilter(t.content)),t.initUploader()
}),e.addListener("contentChange",function(n,i) {
var o="text"==t.returnType?e.getPlainTxt(): e.getContent();
t.$emit("update:content",o)
}),this.ue=e
}},mounted:function() {
var t=this;"textarea"!==this.type&&(this.id=Math.round(1e4*Math.random()),this.$nextTick(function(){t.initEditor()
}),this.loadJs("//image.uc.cn/s/uae/g/08/static/js/aliyun-oss-sdk-4.3.0.min.js"))
}}},function(t,e,n) {
"use strict"
;e.a={name: "dialogs",template:'\n  <el-dialog :title="title" :visible.sync="visible" :size="size" :close-on-click-modal="false" :top="top" :show-close="showClose" :close-on-press-escape="false" @close="onModalClose" @open="onModalOpen" :modal="modal">\n  <slot></slot>\n  </el-dialog>\n  ',watch:{visible:function(t){this.$emit("update:visible",t)
}},props: {
visible: {default:!0
},title: {
default: "标题"
},size: {
default: "large"
},showClose: {
default: !0
},modal: {
default: !0
},top: {
default: "0%"
}}}},function(t,e,n) {
"use strict"
;e.a={name: "modal",template:'\n  <div class="ui-modal" v-show="visible">\n    <div class="ui-modal-inner" v-for="(modal,index) in modalList" :key="index">{{modal.title}}</div>\n  </div>\n  ',data:function(){return{modalList:[{title:"弹窗一"
},{title:"弹窗二"
}],visible:!1
}}}},function(t,e,n) {
"use strict"
;e.a={name: "dropDown",template:'\n\t<div class="ui-dropdown">\n\t<template v-if="! disabled">\n\t\t<el-dropdown split-button size="small" @click="dropdownClick" @command="dropdownCommand">\n\t\t{{button}}\n\t\t<el-dropdown-menu slot="dropdown">\n\t\t<el-dropdown-item :command="item.value" v-for="(item,index) in list" :key="item.value">{{item.name}}</el-dropdown-item>\n\t\t</el-dropdown-menu>\n\t\t</el-dropdown>\n\t</template>\n\t<template v-else>\n\t\t<el-button size="small" disabled >{{button}}</el-button>\n\t</template>\n\t</div>\n\t',props:{button:null,list:Array,disabled:{default:!1
}},methods: {
dropdownClick: function(){this.$emit("click")
},dropdownCommand:function(t) {
this.$emit("command",t)
}}}},function(t,e,n) {
"use strict"
;e.a={name: "trees",template:'\n\t\t<el-tree :data="data" :props="props" :load="load" highlight-current lazy check-strictly ref="root" :render-content="renderContent" :expand-on-click-node="false"> </el-tree>\n\t',props:{data:Array,load:Function,initButton:Function
},data:function() {
return{buttons: []
}},methods: {
renderContent: function(t,e){var n=this.buttons.map(function(n){return t("el-button",{props:{size:"mini",type:n.type||""
},domProps: {
innerHTML: n.name
},on: {
click: function(){n.callback&&n.callback(e.data)
}}})});

return t("div", {
class: {"ui-tree-ctrl":!0
}},[t("span",e.data.name),t("div", {
class: {"ui-right":!0
}},n)])
}},mounted:function() {
this.initButton&&(this.buttons=this.initButton())
}}},function(t,e,n) {
"use strict"
;e.a={name: "file-upload",template:'\n\t\t<div class="ui-upload">\n\n\t\t\t<template v-if="skin === \'list\'">\n\t\t\t\t<el-upload class="upload-demo" :action="action" :multiple="multiple" :data="data" :name="name" :accept="accept" :on-change="$_onChange"  :on-remove="$_onRemove" :on-preview="$_onPreview" :on-error="$_onError" :on-progress="$_onProgress" :show-file-list="false" :auto-upload="false" ref="upload">\n        <el-button slot="trigger">{{btnText || $t(\'upload\',\'点击上传\')}}</el-button>\n\t\t\t\t<span slot="tip" class="ui-upload-tip" v-if="tips">{{tips}}</span>\n\t\t\t\t</el-upload>\n\t\t\t</template>\n\n\t\t\t<template v-else>\n      <el-button @click="isShowUpload = true">{{btnText || $t(\'upload\',\'点击上传\')}}</el-button>\n\t\t\t\n      <el-dialog :title="title || $t(\'uploadAttachment\',\'上传附件\')" :visible.sync="isShowUpload" size="small" :close-on-click-modal="false" top="0%" :show-close="true" :close-on-press-escape="false" @close="onModalClose" @open="onModalOpen">\n      \n      <el-upload class="upload-demo" :action="action" :multiple="multiple" :data="data" :name="name" :accept="accept" :file-list="p_fileList" :on-success="$_onSuccess" :on-remove="$_onRemove" :on-preview="$_onPreview" :on-error="$_onError" :on-progress="$_onProgress" :show-file-list="false">\n      <el-button type="primary" size="small">{{btnText || $t(\'upload\',\'点击上传\')}}</el-button>\n      <div slot="tip" class="el-upload__tip" v-if="tips">{{tips}}</div>\n      </el-upload>\n\n\t\t\t<div slot="footer" class="dialog-footer">\n\t\t\t<el-button type="primary" @click="$_onConfirm">{{$t(\'determine\',\'确 定\')}}</el-button>\n\t\t\t</div>\n\t\t\t</el-dialog>\n      </template>\n      \n      <el-table :data="p_fileList" v-if="p_fileList.length">\n      <el-table-column label="文件名" prop="name" show-overflow-tooltip >\n      <template slot-scope="item">\n      <a href="javascript:;" @click="goDownload(item.row)">{{item.row.name}}</a>\n      </template>\n      </el-table-column>\n      <el-table-column :label="$t(\'note\',\'备注\')" prop="remark" show-overflow-tooltip></el-table-column>\n      <el-table-column :label="$t(\'status\',\'状态\')" prop="state" width="100px">\n      <template slot-scope="item">\n        <span :class="item.row.state">{{formatStatus(item.row)}}</span>\n      </template>\n      </el-table-column>\n      <el-table-column :label="$t(\'operation\',\'操作\')" width="100px">\n      <template slot-scope="item">\n        <el-button size="small" @click="remove(item)">{{$t(\'delete\',\'删除\')}}</el-button>\n      </template>\n      </el-table-column>\n      </el-table>\n\n\t\t\t<el-dialog :visible.sync="isShowPreview" top="0" custom-class="ui-upload-preview" @close="onModalClose" @open="onModalOpen">\n\t\t\t\t<img :src="previewUrl">\n\t\t\t</el-dialog>\n\t\t</div>\n\t',props:{action:{default:"https://reqres.in/api/users"
},btnText: {
default: ""
},tips:String,title: {
default: ""
},multiple: {
default: !0
},data:Object,name: {
default: "file"
},accept:String,onSuccess:Function,onConfirm:Function,onRemove:Function,onPreview:Function,onDownload:Function,beforeRemove:Function,value:Array,skin: {
default: "list"
},acceptType: {
default: "jpg,jpeg,png,gif,zip,rar,7z,pdf,doc,docx,xls,xlsx,bmp"
}},data:function() {
return{isShowUpload: !1,isShowPreview:!1,p_fileList:[],previewUrl:"",timer:null
}},watch: {
value: function(t){t&&t.length&&(console.log("value",t),this.p_fileList=t.map(function(t){return t.url="",t.state="success",t
}))}},methods: {
formatSize: function(t){var e=t.size,n=Math.round(e/1e3),i="KB";
return n>1e3&&(n=Math.round(n/1e3),i="MB"),isNaN(n)?"": n+i
},$_onChange:function(t,e) {
function n(t,e){t.forEach(function(n,i){n.uid===e.uid&&t.splice(i,1)
})}

var i=this;if("ready"===t.status) {
var o=this.p_fileList.filter(function(e){return e.name===t.name
});

this.checkFileType(t.name)?o.length?(this.$_message("请勿重复上传 !"),n(e,t)):(t.state=t.status,this.p_fileList.push(t)):(this.$_message("上传文件格式有误 !"),n(e,t)),clearTimeout(this.timer),this.timer=setTimeout(function() {
i.$refs.upload.submit()
},300)
}

if("success"===t.status) {
var l={remark: "",state:"success"
};

t.response.success||(l.remark=t.response.msg,l.state="failed"),this.p_fileList.forEach(function(e) {
e.uid===t.uid&&(e.state=l.state,e.remark=l.remark,e.percentage=0)
}),this.onSuccess&&this.onSuccess(t.response,t,e),this.onConfirm&&this.onConfirm(this.p_fileList.filter(function(t) {
return"success"===t.state
}))}"fail"===t.status&&this.p_fileList.forEach(function(e) {
e.uid===t.uid&&(e.state="failed",e.percentage=0)
})},$_onProgress:function(t,e,n) {
"uploading"
===e.status&&this.p_fileList.forEach(function(t){t.uid===e.uid&&(t.state=e.status,t.percentage=e.percentage)
})},$_onConfirm:function() {
this.isShowUpload=!1,this.onConfirm&&this.onConfirm(this.p_fileList)
},$_onRemove:function(t,e) {
this.p_fileList=e,"ready"!==t.status&&(t.response&&t.response.data&&(t.id=t.response.data.id),this.onRemove&&this.onRemove(t))
},$_onPreview:function(t) {
this.onPreview&&this.onPreview(t)
},$_onError:function(t,e,n) {
console.warn("upload err",t)
},checkFileType:function(t) {
var e=t.substring(t.lastIndexOf(".")+1).toLowerCase();return-1!==this.acceptType.indexOf(e)
},formatStatus:function(t) {
return{ready: "待上传",uploading:Math.round(t.percentage)+"%",success:"上传成功",failed:"上传失败",fail:"上传失败"
}[t.state]||""
},remove:function(t) {
var e=this;this.confirm("确认删除 ?",function(){function n(e){e.onRemove&&e.onRemove(i),e.p_fileList.splice(t.$index,1),e.onConfirm&&e.onConfirm(e.p_fileList.filter(function(t){return"success"===t.state
}))}

var i=t.row;i.response&&i.response.data&&(i.id=i.response.data.id),e.beforeRemove?e.beforeRemove(i,function() {
n(e)
}):n(e)
})},goDownload:function(t) {
t.response&&t.response.data&&(t.id=t.response.data.id),this.onDownload&&this.onDownload(t)
}},mounted:function() {
this.value&&this.value.length&&(this.p_fileList=this.value.map(function(t){return{name: t.name,url:"",state:"success"
}}))}}},function(t,e,n) {
"use strict"
;e.a={name: "dataTree",template:'\n\t<el-tree :data="data" :props="props" :node-key="nodeKey" :default-checked-keys="checked" show-checkbox ref="dataTree"></el-tree>\n\t',props:{data:Array,props:{default:function(){return{label:"name"
}}},nodeKey: {
default: "name"
}},computed: {
checked: function(){return this.data&&this.data.length?this.data.filter(function(t){return"true"==t.selected
}).map(function(t) {
return t.name
}):[]
}},methods: {
getCheckedNodes: function(){return this.$refs.dataTree.getCheckedNodes()
},selectAll:function(t) {
var e=this;void 0===t&&(t=1);var n=[];1===t&&(n=this.data.map(function(t){return t[e.nodeKey]
})),this.$refs.dataTree.setCheckedKeys(n)
}},data:function() {
return{
}},mounted:function() {
}}},function(t,e,n) {
"use strict"
;e.a={name: "button-set",template:'\n\t<div class="ui-button-set">\n\t<div class="inner" v-show="isSpan">\n\t\t<el-button v-for="btn in list" :key="btn.value" @click="onClick(btn)">{{btn.name}}</el-button>\n\t</div>\n\t<div class="outer">\n\t\t<el-button :type="btnType" @click="onClick">{{btnText}} <i :class="iconClass"></i></el-button>\n\t</div>\n</div>\n\t',props:{list:Array,btnText:{default:"按钮"
},btnType: {
default: ""
}},data:function() {
return{isSpan: !1
}},computed: {
iconClass: function(){return this.isSpan?"el-icon-caret-top":"el-icon-caret-bottom"
}},methods: {
onClick: function(t){if(this.isSpan=!this.isSpan,t.name)return void this.$emit("on-click",t);
this.$emit("on-bottom-click",{name: this.btnText,value:"bottom"
})}}}},function(t,e,n) {
"use strict"
;e.a={name: "cascader-dialog",template:'\n\t<el-dialog :title="currentTitle" :visible.sync="isShow" size="large" :close-on-click-modal="false" top="0%" :show-close="false" :close-on-press-escape="false" @close="onModalClose" @open="onModalOpen">\n\n\t<el-row :gutter="10">\n\t\t<el-col :span="10" class="ui-cascader-dialog-search">\n\t\t\t<input type="text" class="el-input__inner" placeholder="请输入关键并回车搜索" v-model="keyword" @keyup.enter="doSearch">\n\t\t\t<i :class="{\'el-icon-circle-cross\': keyword,\'el-icon-search\':!keyword}" @click="clearFilter"></i>\n\t\t</el-col>\n\t\t<el-col :span="12">\n\t\t\t<el-button type="primary" @click="doSearch">搜索</el-button>\n\t\t</el-col>\n\t</el-row>\n\n\t<template v-if="isSearch">\n\t\t<el-table :data="searchResult" height="240" highlight-current-row @current-change="onNameChange" v-loading.body="loading">\n\t\t<el-table-column label="用户列表" prop="fullName"></el-table-column>\n\t\t</el-table>\n\t</template>\n\n\t<template v-else>\n\t<el-row v-loading.body="loading">\n\t\t<el-col :span="10">\n\t\t\t<el-table :data="typeList" height="240" highlight-current-row @current-change="onTypeChange">\n\t\t\t\t<el-table-column label="技能组" prop="name"></el-table-column>\n\t\t\t</el-table>\n\t\t</el-col>\n\t\t<el-col :span="14">\n\t\t\t<el-table :data="nameList" height="240" highlight-current-row @current-change="onNameChange">\n\t\t\t\t<el-table-column label="用户列表" prop="fullName"></el-table-column>\n\t\t\t</el-table>\n\t\t</el-col>\n\t</el-row>\n\t</template>\n\n\t<div slot="footer" class="dialog-footer">\n\t\t<el-button @click="isShow = false">取 消</el-button>\n\t\t<el-button type="primary" @click="onConfirm">确 定</el-button>\n\t</div>\n</el-dialog>\n\t',props:{urls:{default:function(){return{getType:"",getList:"",search:""
}}},visible: {
default: !1
},value:null,title: {
default: "转单"
}},watch: {
visible: function(t){!0===t&&(this.isShow=!0,this.initDialog())
},isShow:function(t) {
!1===t&&this.$emit("update:visible",!1)
},value:function(t) {
this.currentInfo=t,this.currentTitle=this.title+" "+t.name
}},data:function() {
return{isShow: !1,loading:!1,typeList:[],nameList:[],searchResult:[],currentInfo:{
},currentTitle:"",keyword:"",isSearch:!1
}},methods: {
doSearch: function(){var t=this;
if(this.keyword){this.loading=!0,this.isSearch=!0;var e=["kfIdentity="+this.currentInfo.value,"fullName="+this.keyword].join("&");this.get(this.urls.search+"?"+e,function(e){t.searchResult=e.data,t.loading=!1
})}

else this.clearFilter()
},clearFilter:function() {
this.isSearch&&(this.isSearch=!1,this.keyword="",this.searchResult=[])
},initDialog:function() {
this.typeList=[],this.nameList=[],this.getType()
},getType:function() {
var t=this;this.loading=!0;var e=this.value.value;this.get(this.urls.getType+e,function(e){t.typeList=e.data,t.loading=!1
})},onTypeChange:function(t) {
var e=this;this.loading=!0,this.currentInfo.type=t,this.get(this.urls.getList+t.id,function(t){e.nameList=t.data,e.loading=!1
})},onNameChange:function(t) {
this.currentInfo.result=t
},onConfirm:function() {
this.$emit("input",this.currentInfo),this.isShow=!1,this.$emit("on-confirm",this.currentInfo)
}}}},function(t,e,n) {
"use strict"
;e.a={name: "cascader",template:'\n\t<div class="ui-cascader">\n\n\t<template v-if="skin === \'button\'">\n\t<el-button type="" @click="open">弹窗</el-button>\n\t</template>\n\t\n\t<template v-if="skin === \'input\'">\n\t<el-row type="flex" :gutter="5">\n    <el-col style="position:relative">\n    <div class="inp">\n    <i class="el-icon-circle-cross" @click="clear" v-if="pathDisplay"></i>\n    <input type="text" class="el-input__inner" :placeholder="$t(\'selectProblemCategory\',\'请选择问题分类\')" v-model="pathDisplay" @input="onInput" @keyup.down="keyDown" @keyup.up="keyUp" @keyup.enter="goSearch(\'outside\')">\n    </div>\n    <div class="result" v-if="showResult && !isShowModal">\n    <div class="empty" v-if="!searchResult.length">无结果</div>\n    <a href="javascript:;" v-for="(n,i) in searchResult" @click="searchCheck(n,\'outside\')" :class="{active: activeIndex == i}">{{n.name}}</a>\n    </div>\n    </el-col>\n\t\t<el-col style="width:120px;"><el-button @click="open" :type="btnType" :size="btnSize">{{btnText || $t(\'problemCategory\',\'问题分类\')}}</el-button></el-col>\n\t</el-row>\n\t</template>\n\n\t<el-dialog :title="title || $t(\'problemCategory\',\'问题分类\')" :visible.sync="isShowModal" size="large" :close-on-click-modal="false" @close="modalClose" @open="onModalOpen" :top="top" :modal="locModal">\n    <div class="ui-cascader-bd">\n      <div class="ui-cascader-search">\n      <el-row style="margin: 0 0 10px;">\n      <el-col :span="12" style="margin:0 10px 0 0;">\n      <div class="inp">\n      <i class="el-icon-circle-cross" v-if="keyword" @click="clear"></i>\n      <input :placeholder="$t(\'keyword\',\'关键字\')" v-model="keyword" @keyup.enter="goSearch" class="el-input__inner" @keyup.down="keyDown" @keyup.up="keyUp">\n      </div>\n      </el-col>\n      <el-col :span="10">\n      <el-button type="primary" @click="goSearch">{{$t(\'search\',\'搜索\')}}</el-button>\n      </el-col>\n      </el-row>\n      <div class="result" v-if="showResult">\n      <div class="empty" v-if="!searchResult.length">无结果</div>\n      <a href="javascript:;" v-for="(n,i) in searchResult" @click="searchCheck(n)" :class="{active: activeIndex == i}">{{n.name}}</a>\n      </div>\n      </div>\n\t\t\t<el-row type="flex" v-loading="loading">\n\t\t\t\t<el-col :span="5" v-for="(item,index) in list" :key="index">\n\t\t\t\t\t<el-table :data="item" height="400" highlight-current-row  :show-header="false" @cell-click="cellClick" @cell-mouse-enter="cellEnter(index)" @cell-dblclick="cellDbClick">\n\t\t\t\t\t\t<el-table-column prop="name" class="active" show-overflow-tooltip>\n\t\t\t\t\t\t<template scope="scope">\n\t\t\t\t\t\t<div class="ui-cascader-cell">\n\t\t\t\t\t\t<span>{{ scope.row.title || scope.row.name }}</span>\n\t\t\t\t\t\t<span class="el-icon-arrow-right" v-if="scope.row.leaf"></span>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</template>\n\t\t\t\t\t\t</el-table-column>\n\t\t\t\t\t</el-table>\n\t\t\t\t</el-col>\n\t\t\t</el-row>\n\t\t</div>\n\t</el-dialog>\n\t</div>\n\t',props:{top:{default:"0%"
},value:null,skin: {
default: "input"
},title: {
default: ""
},btnType:null,btnSize:null,btnText: {
default: ""
},modal: {
default: function(){return!0
}},urls: {
default: function(){return{root:"/system/basics/problemCategory/queryRootByAccount.do",leaf:"/system/basics/problemCategory/queryLeafByAccount?parent=",search:"/system/basics/problemCategory/searchByAccount.do?name=",searchByName:"/system/basics/problemCategory/searchByName?name=",template:"/system/basics/problemTemplate/queryProblemTemplate?problemCategoryId="
}}}},data:function() {
return{list: [],isShowModal:!1,locModal:this.modal,currentIndex:0,path:[],pathDisplay:"",loading:!1,keyword:"",activeKeyword:"",searchResult:[],activeIndex:0,showResult:!1,inputTimer:null
}},methods: {
onInput: function(){var t=this;
clearTimeout(this.inputTimer),this.inputTimer=setTimeout(function(){if(""==t.pathDisplay)return void(t.showResult=!1);t.loading=!0,t.activeKeyword=t.pathDisplay,t.get(t.urls.searchByName+t.activeKeyword+"&isNeedNotLeaf=false",function(e){t.loading=!1,t.showResult=!0,200==e.code&&(t.searchResult=e.data)
})},1e3)
},clear:function() {
this.showResult=!1,this.pathDisplay="",this.keyword="",this.$emit("clear")
},keyUp:function() {
this.activeIndex=0==this.activeIndex?this.searchResult.length-1: this.activeIndex-1
},keyDown:function() {
this.activeIndex=this.activeIndex==this.searchResult.length-1?0: this.activeIndex+1
},searchCheck:function(t,e) {
var n=this;if(void 0===e&&(e="inside"),this.showResult=!1,this.searchResult=[],this.activeIndex=0,this.list=[],"outside"==e)return this.pathDisplay=t.name,this.$emit("input",t),void this.$emit("confirm",t);this.getRoot(function(){var e=t.problemCategoryIds.split(","),i=function(t){n.loading=!0,n.get(n.urls.leaf+e[t],function(o){var l=function(){var i="";return n.list[t-1].forEach(function(n){n.code==e[t]&&(i=n.name)
}),i
}();

n.path.push(l),n.loading=!1,n.list.push(o.data),++t<e.length&&i(t)
})};

i(1)
})},goSearch:function(t) {
var e=this;if(void 0===t&&(t="inside"),this.searchResult.length&&this.showResult)return void this.searchCheck(this.searchResult[this.activeIndex],t);this.loading=!0,this.activeKeyword=this.keyword,this.get(this.urls.searchByName+this.activeKeyword+"&isNeedNotLeaf=true",function(t){e.loading=!1,e.showResult=!0,200==t.code&&(e.searchResult=t.data)
})},open:function() {
this.showResult=!1,this.searchResult=[],this.list=[],this.getRoot(),this.isShowModal=!0
},modalClose:function() {
!0===this.locModal&&this.onModalClose(),this.keyword="",this.list=[],this.path=[]
},onconfirm:function() {
this.isShowModal=!1
},cellEnter:function(t) {
this.currentIndex=t
},cellClick:function(t,e,n,i) {
if(!this.loading&&!t.title){if(this.list.splice(this.currentIndex+1),this.path.splice(this.currentIndex),this.path.push(t.name),!t.leaf)return void this.getTemplate(t.id);this.getLeaf(t)
}},cellDbClick:function(t,e,n,i) {
if(t.title)return this.$emit("input",t),this.$emit("confirm",t),this.pathDisplay=this.path.join(","),this.isShowModal=!1,void(this.list=[]);t.leaf||(this.$emit("input",t),this.$emit("confirm",t),this.pathDisplay=this.path.join(","),this.isShowModal=!1,this.list=[])
},getTemplate:function(t) {
var e=this;this.get(this.urls.template+t,function(t){e.list.push(t.data)
})},getLeaf:function(t) {
var e=this;!t.leaf||this.list.length>=5||this.get(this.urls.leaf+t.id,function(t){e.list.push(t.data)
})},getRoot:function(t) {
var e=this;this.loading=!0,this.get(this.urls.root,function(n){e.loading=!1,e.list.push(n.data),t&&t()
})}}}},function(t,e,n) {
"use strict"
;e.a={name: "content-popover",template:'\n  <el-popover :content="content" placement="top" trigger="hover" width="300" :open-delay="300" :disabled="!content">\n  <em slot="reference" class="ui-form-item-content">{{content}}</em>\n  </el-popover>\n  ',props:{content:null
},methods: {
format: function(t){
}}}},function(t,e,n) {
"use strict"
;e.a={name: "cells",template:'\n  <div class="ui-cells">\n  <slot></slot>\n  </div>\n  ',props:{labelWidth:null
}}},function(t,e,n) {
"use strict"
;e.a={name: "cells-items",template:'\n  <div class="item">\n  <label class="label" :style="labelStyle">{{label}}</label>\n  <div class="content" :style="contentStyle">\n  <slot>\n  <template v-if="!content">-</template>\n  <template v-else-if="content.length > 8">\n  <content-popover :content="content"></content-popover>\n  </template>\n  <template v-else>{{content}}</template>\n  </slot>\n  </div>\n  </div>\n  ',props:{label:null,content:null
},computed: {
labelStyle: function(){if(this.$parent.labelWidth)return{width:this.$parent.labelWidth
}},contentStyle:function() {
if(this.$parent.labelWidth)return{marginLeft: parseInt(this.$parent.labelWidth)+10+"px"
}}}}},function(t,e,n) {
"use strict"
;var i=Math.round(1e3*Math.random());e.a={name: "chart",template:'<div :id="id" class="ui-chart" :style="{height: opts.height}"></div>',props:{data:null,type:{default:"line"
},opts: {
default: function(){return{height:"400px"
}}}},data:function() {
return{id: "chart_"+i,chart:{
}}},watch: {
data: function(t){t&&(this.chart.clear(),this.changeData(t))
}},methods: {
clear: function(){this.chart.clear()
},changeData:function(t) {
var e={
};

e="pie"==this.type? {
title: {text:t.title||""
},legend: {
data: t.legend,type:"scroll",left:"120px",selected:t.selected
},tooltip: {
trigger: "item",formatter:"{a} <br/>{b} : {c} ({d}%)"
},toolbox: {
feature: {saveAsImage:{
}},right:"20px",top:"20px"
},series:t.series
}

: {
title: {text:t.title||""
},legend: {
data: t.legend,type:"scroll",left:"120px"
},tooltip: {
axisPointer: {type:"cross"
}},toolbox: {
feature: {saveAsImage:{
}},right:"20px",top:"20px"
},xAxis: {
data: t.xAxis
},yAxis: {
},series:t.series
},this.chart.setOption(e)
},init:function() {
this.data;if("pie"==this.type){var t=echarts.init(document.getElementById(this.id),"macarons");this.chart=t,this.chart.clear(),this.changeData(this.data)
}

var t=echarts.init(document.getElementById(this.id),"macarons");this.chart=t
}},created:function() {
this.data.chart_id&&(this.id=this.data.chart_id)
},mounted:function() {
this.init()
}}},function(t,e,n) {
"use strict"
;e.a={name: "ui-select",template:'\n  <div class="ui-select">\n  <el-input :value="text" :placeholder="placeholder" readonly @focus="openContent" icon="caret-bottom" @click="openContent"></el-input>\n\n  <transition name="el-zoom-in-top">\n  <div class="ui-select-inner" v-show="showContent" :style="{right: innerRight}">\n  <el-row v-if="showSearch">\n  <el-col :span="12"><el-input v-model="keyword" size="small" placeholder="关键字" :icon="keyword ? \'circle-cross\' : \'\'" :on-icon-click="clearKeyword"></el-input></el-col>\n  <el-col :span="12">\n  <el-button-group>\n  <el-button size="small" @click="checkAll">全选</el-button>\n  <el-button size="small" @click="inverse">反选</el-button>\n  <el-button size="small" @click="clearAll">清空</el-button>\n  </el-button-group>\n  </el-col>\n  </el-row>\n  <el-checkbox-group v-model="selectList" class="ui-select-list" @change="onChange" v-loading.body="loading">\n  <template v-for="n in searchList">\n  <el-checkbox :label="n">{{n[label]}}<span class="info" v-if="info">{{n[info]}}</span></el-checkbox>\n  </template>\n  </el-checkbox-group>\n  </div>\n  </transition>\n</div>\n  ',props:{data:Array,keyName:String,label:String,val:{default:"id"
},info:null,value:null,placeholder:String,checkedList:Array,load:Function,showSearch: {
default: function(){return!0
}}},data:function() {
return{selectList: [],keyword:"",showContent:!1,loading:!1,innerRight:"auto"
}},watch: {
data: function(t){this.syncValue()
},value:function(t,e) {
this.syncValue()
}},computed: {
text: function(){var t=this;
return this.selectList.length?this.selectList.map(function(e){return e[t.label]
}).join(","):""
},searchList:function() {
var t=this;return this.keyword?this.data.filter(function(e){return e[t.label].toLowerCase().indexOf(t.keyword.toLowerCase())>-1||t.info&&e[t.info].toLowerCase().indexOf(t.keyword.toLowerCase())>-1
}):this.data
}},methods: {
checkAll: function(){this.selectList=this.searchList,this.passValue()
},clearAll:function() {
this.selectList=[],this.passValue()
},inverse:function() {
var t=this;this.selectList=this.searchList.filter(function(e){return!t.selectList.filter(function(n){return e[t.val]==n[t.val]
}).length
}),this.passValue()
},clearKeyword:function() {
this.keyword=""
},syncValue:function() {
var t=this;this.value?this.selectList=this.data.filter(function(e){return t.value.split(",").filter(function(n){return e[t.val]==n
}).length
}):this.selectList=[]
},passValue:function() {
var t=this;this.$emit("input",this.selectList.map(function(e){return e[t.val]
}).join(",")),this.$emit("update:checkedList",this.selectList)
},onChange:function() {
this.passValue()
},openContent:function() {
this.showContent=!this.showContent,this.innerRight=document.body.clientWidth-function(t){for(var e=t.offsetLeft,n=t.offsetParent;null!==n;)e+=n.offsetLeft,n=n.offsetParent;return e
}(this.$el)<290?0:"auto",this.data&&this.data.length||this.load&&this.load()
}},mounted:function() {
var t=this;this.syncValue(),document.addEventListener("click",function(e){var n=e.target;t.$el.contains(n)||(t.showContent=!1)
})}}},function(t,e,n) {
"use strict"
;e.a={name: "ui-select-multi",template:'\n    <el-select v-model="result" multiple filterable :remote="remote" :placeholder="placeholder" :remote-method="remoteMethod" :loading="loading" @change="onChange" :disabled="disabled">\n    <el-option v-for="item in options" :key="item[val]" :label="item[label]" :value="item[val]">\n    </el-option>\n    </el-select>\n  ',data:function(){return{result:[],timer:null,loading:!1
}},props: {
options: Array,remote:{default:!1
},value:Array,label: {
default: "name"
},val: {
default: "value"
},placeholder: {
default: "请输入关键词"
},disabled: {
default: !1
}},watch: {
value: {handler:function(t,e){if(e){if(t.toString()==e.toString())return;
t.length&&(this.result=t),t.length||(this.result=[])
}

else this.result=t
},immediate:!0
}},methods: {
onChange: function(t){this.$emit("input",t)
},remoteMethod:function(t) {
var e=this;clearTimeout(this.timer),this.timer=setTimeout(function(){e.$emit("on-search",t)
},500)
}}}},function(t,e,n) {
"use strict"
;e.a={name: "ui-treeroot-item",template:'\n      <div class="ui-treeroot-item">\n      <div class="ui-treeroot-content" @click="toggle" @dblclick="dblclick">\n      <i class="ui-treeroot-icon" :class="iconClass"></i>\n      {{model.name}}\n      <slot :model="model"></slot>\n      </div>\n      <div class="ui-treeroot-children" v-show="show" v-if="!model[leafKey]">\n        <ui-treeroot-item :key="index" v-for="(n,index) in model.children" :model="n" :load="load" :leaf-key="leafKey" @on-confirm="onConfirm" @loading="onLoadingChange" @on-dblclick="onDblclick">\n          <template slot-scope="_">\n            <slot :model="_.model"></slot>\n          </template>\n        </ui-treeroot-item>\n      </div>\n      </div>\n      ',props:{model:Object,load:Function,leafKey:{default:"leaf"
}},data:function() {
return{show: !1,timer:null
}},computed: {
iconClass: function(){return this.model[this.leafKey]?"el-icon-document":this.show?"el-icon-caret-bottom":"el-icon-caret-right"
}},methods: {
dblclick: function(){clearTimeout(this.timer),this.$emit("on-dblclick",this.model),this.model[this.leafKey]&&this.$emit("on-confirm",this.model)
},onConfirm:function(t) {
this.$emit("on-confirm",t)
},onDblclick:function(t) {
this.$emit("on-dblclick",t)
},onLoadingChange:function(t) {
this.$emit("loading",t)
},toggle:function() {
var t=this;clearTimeout(this.timer),this.timer=setTimeout(function(){t.model[t.leafKey]||(t.show=!t.show,t.model.children||(t.$set(t.model,"children",[]),t.$emit("loading",!0),t.load(t.model,function(e){t.model.children=e,t.$emit("loading",!1)
})))},300)
}}}},function(t,e,n) {
"use strict"
;e.a={name: "ui-treeroot-wrap",template:'\n  <div class="ui-treeroot-wrap">\n  <div class="ui-treeroot-wrap-hd">\n  <input type="text" placeholder="输入关键字回车搜索" class="el-input__inner" v-model="keyword" @keyup.enter="onKeyup">\n  <i :class="{\'el-icon-circle-cross\':keyword, \'el-icon-search\' : !keyword}" @click="resetSearch"></i>\n  </div>\n  <div class="ui-treeroot-wrap-bd" v-loading.body="loading">\n  <template v-if="!isSearch">\n  <ui-treeroot-item :key="index" v-for="(n,index) in data" :model="n" :load="load" :leaf-key="leafKey" @on-confirm="onConfirm" @loading="onLoadingChange"> </ui-treeroot-item>\n  </template>\n\n  <template v-else>\n  <ui-treeroot-item :key="index" v-for="(n,index) in searchResult" :model="n" :load="load" :leaf-key="leafKey" @on-confirm="onConfirm" @loading="onLoadingChange"> </ui-treeroot-item>\n  <div class="empty" v-if="!hasResult">暂无数据</div>\n  </template\n\n  </div>\n  </div>\n  ',props:{data:Array,load:Function,search:Function,leafKey:{default:"leaf"
}},data:function() {
return{keyword: "",isSearch:!1,timer:null,hasResult:!0,searchResult:[],loading:!1
}},methods: {
onLoadingChange: function(t){this.loading=t
},resetSearch:function() {
this.isSearch=!1,this.keyword="",this.hasResult=!0
},onConfirm:function(t) {
this.$emit("on-confirm",t)
},onKeyup:function() {
var t=this;this.search&&this.search(this.keyword,function(e){if(t.isSearch=!0,!e.length)return void(t.hasResult=!1);t.searchResult=e
})},onInput:function() {
var t=this;clearTimeout(this.timer),this.timer=setTimeout(function(){t.search&&t.search(t.keyword,function(e){if(t.isSearch=!0,!e.length)return void(t.hasResult=!1);t.searchResult=e
})},1e3)
}}}},function(t,e,n) {
"use strict"
;var i=window;e.a={data: function(){return{activeUrl:""
}},methods: {
scrollBack: function(){var t=Number(this.session(this.activeUrl+"_top"));
document.scrollingElement.scrollTop=t,console.log(t)
}},mounted:function() {
var t=this;this.activeUrl=parent.document.querySelector("iframe").name;var e=null;i.onscroll=function(n){e&&clearTimeout(e),e=setTimeout(function(){t.session(t.activeUrl+"_top",document.scrollingElement.scrollTop)
},500)
}}}},function(t,e,n) {
"use strict"
;function i(t){return parent?window.parent.app: t
}

function o(t,e) {
void 0===e&&(e="yyyy-MM-dd");var n=new Date(t);if(isNaN(n.getTime()))return"";var i={"y+": n.getFullYear(),"M+":n.getMonth()+1,"d+":n.getDate(),"h+":n.getHours(),"m+":n.getMinutes(),"s+":n.getSeconds(),"q+":Math.floor((n.getMonth()+3)/3),"S+":n.getMilliseconds()
};

for(var o in i)if(new RegExp("("+o+")").test(e))if("y+"==o)e=e.replace(RegExp.$1,(""+i[o]).substr(4-RegExp.$1.length));else if("S+"==o) {
var l=RegExp.$1.length;l=1==l?3: l,e=e.replace(RegExp.$1,("00"+i[o]).substr((""+i[o]).length-1,l))
}

else e=e.replace(RegExp.$1,1==RegExp.$1.length?i[o]:("00"+i[o]).substr((""+i[o]).length));return e
}

var l=this&&this.__assign||Object.assign||function(t) {
for(var e,n=1,i=arguments.length;n<i;n++){e=arguments[n];for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])
}

return t
};

e.a= {
data: function(){return{validateRules:{text:{validator:function(t,e,n){""===e||void 0===e||null===e?n(new Error("不能为空")):n()
},trigger:"blur"
},select: {
validator: function(t,e,n){""===e||void 0===e||null===e?n(new Error("请选择")):n()
},trigger:"change"
}}}},filters: {
date: function(t,e){return o(t,e)
}},methods: {
initBarScroll: function(){var t=function(t,e){var n=e.parentNode;
n.lastChild==e?n.appendChild(t): n.insertBefore(t,e.nextSibling)
};

this.$nextTick(function() {
var e=this,n=document.querySelector(".ui-table"),i=document.querySelector(".el-table__body").clientWidth,o=document.createElement("div");o.setAttribute("class","ui-table-scrollbar"),o.innerHTML='<div class="scrollbar"> </div>',o.onscroll=function(t){e.onBarScroll(t)
},t(o,n),o.querySelector(".scrollbar").setAttribute("style","width: "+i+"px")
})},onBarScroll:function(t) {
document.querySelector(".el-table__body-wrapper").scrollLeft=t.target.scrollLeft
},$t:function(t,e) {
if(void 0===e&&(e=""),!(location.host.match("cswb.uc.cn")||location.host.match("localhost")||location.host.match("csworkbenchweb.sz.uae.uc.cn")))return e;var n={
},i=localStorage.activeLang||"zh";if(localStorage.lang)return n=JSON.parse(localStorage.lang),n[i][t]||"";this.getLocale()
},getLocale:function(t) {
localStorage.lang&&t&&t(JSON.parse(localStorage.lang)),this.get("/locale",function(e){200==e.code&&(localStorage.lang||t&&t(e.data),localStorage.lang=JSON.stringify(e.data))
})},initWA:function() {
if(!document.getElementById("trackPageView")){var t=window._paq||[],e=(this.getUuid(),navigator.userAgent.toLocaleLowerCase()),n=function(){var t=["mac","windows"];return t=t.filter(function(t){return e.match(t)
}),t.length?t.join(""):"other"
}(),i=function() {
for(var t=["chrome","safari","trident"],n="other",i=0;i<t.length;i++){var o=t[i];if(e.match(o)){n=o;break
}}

return n
}(),o=function() {
var e=1;return function(n,i,o){void 0===o&&(o="page"),t.push(["setCustomVariable",e,n,i,o]),e++
}}();!function() {
var e=location.protocol+"//track.uc.cn/";t.push(["setSiteId","2a1aa6c4f8c3"]),t.push(["setDocumentTitle",document.title||"客服工作台"]),t.push(["setTrackerUrl",e+"collect?uc_param_str=dnfrcpve"]),o("ua",navigator.userAgent),o("la",navigator.languages.join(",")),o("screen",screen.width+"x"+screen.height),o("browser",i),o("system",n),o("url",location.href),t.push(["trackPageView"]),window._paq=t;var l=document,s=l.createElement("script"),a=l.getElementsByTagName("script")[0];s.type="text/javascript",s.defer=!0,s.async=!0,s.src=e+"uaest.js",s.id="trackPageView",a.parentNode.insertBefore(s,a)
}()}},getUuid:function() {
if(localStorage.uuid)return localStorage.uuid;var t="xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";return t=t.replace(/[xy]/g,function(t){var e=16*Math.random()|0;return("x"==t?e: 3&e|8).toString(16)
}),localStorage.uuid=t,t
},DateFormat:function(t,e) {
void 0===e&&(e="yyyy-MM-dd");var n=new Date(t);isNaN(n.getTime())&&(n=new Date(Number(t)));var i={"y+": n.getFullYear(),"M+":n.getMonth()+1,"d+":n.getDate(),"h+":n.getHours(),"m+":n.getMinutes(),"s+":n.getSeconds(),"q+":Math.floor((n.getMonth()+3)/3),"S+":n.getMilliseconds()
};

for(var o in i)if(new RegExp("("+o+")").test(e))if("y+"==o)e=e.replace(RegExp.$1,(""+i[o]).substr(4-RegExp.$1.length));else if("S+"==o) {
var l=RegExp.$1.length;l=1==l?3: l,e=e.replace(RegExp.$1,("00"+i[o]).substr((""+i[o]).length-1,l))
}

else e=e.replace(RegExp.$1,1==RegExp.$1.length?i[o]:("00"+i[o]).substr((""+i[o]).length));return e
},formatHtml:function(t) {
return t
},formatDate:function(t,e,n) {
return this.DateFormat(n)
},formatDateTime:function(t,e,n) {
return n?this.DateFormat(n,"yyyy-MM-dd hh:mm:ss"): " "
},formatStatus:function(t,e,n) {
return"boolean"==typeof n?n?"有效": "无效":"number"==typeof n?0===n?"无效":"有效":"string"==typeof n?"0"===n?"无效":"有效":void 0
},loadJs:function(t,e) {
var n=document.querySelectorAll("script");if([].slice.call(n).filter(function(e){return e.getAttribute("src")===t
}).length)return void(e&&e());var i=document,o=i.createElement("script"),l=i.getElementsByTagName("script")[0];o.type="text/javascript",o.src=t,l.parentNode.insertBefore(o,l),o.onload=function() {
e&&e()
}},confirm:function(t,e) {
var n=i(this),o={type: "warning"
};"function"==typeof e&&(o.callback=function(t) {
"confirm"
===t&&e&&e()
}),"object"==typeof e&&(o=l( {
},o,e)),n.$confirm(t,o)
},alert:function(t,e) {
i(this).$alert(t,{callback: function(){e&&e()
},type:"warning",customClass:"ui-message",closeOnClickModal:!1
})},closeTab:function() {
i(this).$bus.emit("closeTab")
},link:function(t,e,n) {
void 0===e&&(e="新标签"),void 0===n&&(n="_blank");var o=i(this),l=parent&&window.parent.API?window.parent.API: "";
o.$bus.emit("addTab",{url: l+t,title:e,target:n
})},$_message:function(t,e,n,o) {
void 0===e&&(e="success"),void 0===n&&(n=null),void 0===o&&(o=!1);var l=i(this),s=3e3;o&&(s=0),l.$message({message: t,type:e,duration:s,showClose:o,onClose:function(){n&&n()
}})},message:function() {
for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];this.$_message.apply(this,t)
},onModalClose:function() {
i(this).$bus.emit("modalToggle",!1)
},onModalOpen:function() {
i(this).$bus.emit("modalToggle",!0)
},refresh:function() {
location.reload()
},ajax:function(t,e) {
function n(t,e,n){t[e]=t[e]||n
}

function o(t,n) {
return void 0===n&&(n=null),function(){u||(e(void 0===d.status?t: d.status,0===d.status?"Error":d.response||d.responseText||n,d),u=!0)
}}

var l=(i(this),window),s=["responseType","withCredentials","timeout","onprogress"],a=t.headers|| {
},r=t.body,c=t.method||(r?"POST":"GET"),u=!1;r&&"object"==typeof r&&(r=function() {
var t=[];for(var e in r)t.push(e+"="+encodeURIComponent(r[e]));return t.join("&")
}());

var d=function(t) {
return t&&l.XDomainRequest&&!/MSIE 1/.test(navigator.userAgent)?new XDomainRequest: l.XMLHttpRequest?new XMLHttpRequest:void 0
}(t.cors);d.open(c,t.url,!0);var h=d.onload=o(200);d.onreadystatechange=function() {
4===d.readyState&&h()
},d.onerror=o(null,"Error"),d.ontimeout=o(null,"Timeout"),d.onabort=o(null,"Abort"),r&&(n(a,"X-Requested-With","XMLHttpRequest"),l.FormData&&r instanceof l.FormData||n(a,"Content-Type","application/x-www-form-urlencoded"));for(var p=0,f=s.length,m=void 0;p<f;p++)m=s[p],void 0!==t[m]&&(d[m]=t[m]);for(var m in a)d.setRequestHeader(m,a[m]);d.send(r)
},post:function(t,e,n) {
var i=parent&&window.parent.API?window.parent.API: "";
for(var o in e)if(e.hasOwnProperty(o)){var l=e[o];e[o]=null===l||void 0===l?"": l
}

this.ajax( {
url: i+t,body:e
},function(t,e,i) {
try{e=JSON.parse(e)
}

catch(t) {
}

if(200===t)return void(n&&n(e,i))
})},get:function(t,e) {
var n=parent&&window.parent.API?window.parent.API: "";
this.ajax({url: n+t
},function(t,n,i) {
try{n=JSON.parse(n)
}

catch(t) {
}

200===t&&e&&e(n,i)
})},$fire:function(t,e) {
window.parent.document.querySelectorAll("iframe").forEach(function(n){var i=n.name,o=parent[i];o.window.app&&o.window.app.$emit(t,e)
})},getCookiesByKey:function(t) {
var e=document.cookie.split(";"),n={
};

return e.forEach(function(t) {
t=t.trim();var e=t.split("=");n[e[0]]=e[1]
}),n[t]
},session:function(t,e) {
if(e){if("string"!=typeof e)try{e=JSON.stringify(e)
}

catch(t) {
console.log(t)
}

return void(sessionStorage[t]=e)
}

var n=sessionStorage[t];try {
n=JSON.parse(n)
}

catch(t) {
}

return n
},getQuery:function(t) {
var e=location.search.substr(1);if(t){var n=new RegExp("(^|&)"+t+"=([^&]*)(&|$)","i"),i=e.match(n);return null!=i?decodeURI(i[2]): null
}

return e
}},mounted:function() {
this.initWA()
}}},function(t,e,n) {
"use strict"
;var i=new Vue;i.on=function(){for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];this.$on.apply(this,t)
},i.emit=function() {
for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];this.$emit.apply(this,t)
},i.off=function() {
for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];this.$off.apply(this,t)
},i.once=function() {
for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];this.$once.apply(this,t)
},e.a=i
}]);