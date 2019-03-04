# nlp项目说明文档

## 接口说明

1.CRF标签提取

*CRF.getWordLabel(String query, Forest zbjdic, ModelParse MP)方法
 *方法说明：crf序列标注获取最优路径标签
  * 输入参数：
 
	| 名称 | 含义 | 备注 |
	|:-------:|:-------:|:-------:|
	|query|输入文本|无|
	|zbjdic|zbjsmall.dic|初始化|
	|MP|crf模型解析|无|
 * 返回值说明：Map(Term, String)	——>	重庆/ns	LA
 
*CRF.getLabelScore(String query, Forest zbjdic, ModelParse MP)方法
 *方法说明：crf序列标注获取最优路径标签和分数
  * 输入参数：
 
	| 名称 | 含义 | 备注 |
	|:-------:|:-------:|:-------:|
	|query|输入文本|无|
	|zbjdic|zbjsmall.dic|初始化|
	|MP|crf模型解析|无|
 * 返回值说明：Map(Term, CrfInfo)	——>	重庆/ns	LA	1.4127132072785271

2.依存句法分析
待添加...
