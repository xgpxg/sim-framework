import dictConfig from './dict-config';
import autocompleteConfig from './autocomplete-config';
import treeConfig from './tree-config';
import categoryConfig from './category-config';

const heyuiConfig = () => {
  const staticDict = dictConfig();
  Object.keys(staticDict).forEach((key) => {
    HeyUI.addDict(key, staticDict[key]);
  });
  //字典项key和value配置
  HeyUI.config('dict.keyName', 'itemCode');
  HeyUI.config('dict.titleName', 'itemText');

  HeyUI.config('autocomplete.configs', autocompleteConfig());
  HeyUI.config('tree.configs', treeConfig());
  HeyUI.config('category.configs', categoryConfig());

  HeyUI.config('menu', {
    keyName: 'key',
    titleName: 'title',
    childrenName: 'children'
  });
};

export default heyuiConfig;
