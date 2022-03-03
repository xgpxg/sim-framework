import Ajax from './ajax';

const Request = {
  User: {
    info() {
      return Ajax.get('/account/info');
    }
  },
  Dict: {
    /**
     * 加载字典配置项
     * @param code
     * dic_code
     * @returns {*}
     */
     get(code) {
      return Ajax.get('/manager/common/dic',{code:code});
    }
  },
  Home: {
    getMessageList() {
      return {
        'status': 200,
        'body': [{
          'id': 1,
          'isReaded': false,
          'title': '任务名称1',
          'description': '你需要在某年某月完成某某任务'
        }, {
          'id': 2,
          'isReaded': false,
          'title': '任务名称2',
          'description': '你需要在某年某月完成某某任务'
        }, {
          'id': 3,
          'isReaded': true,
          'title': '任务名称3',
          'description': '你需要在某年某月完成某某任务'
        }, {
          'id': 4,
          'isReaded': true,
          'title': '任务名称4',
          'description': '你需要在某年某月完成某某任务'
        }, {
          'id': 5,
          'isReaded': true,
          'title': '任务名称5',
          'description': '你需要在某年某月完成某某任务'
        }]
      };
    }
  },
  Login: {
    login(param) {
      return Ajax.postJson('/login', param);
    },
    logout(param) {
      return Ajax.post('/logout', param);
    }
  },
  TestData:{
    getData(){
      return Ajax.get('http://localhost:5008/qryCrawlerList')
    }
  }
};

export default Request;
