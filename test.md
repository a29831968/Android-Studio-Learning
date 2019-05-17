# C++ cin.clear() / cin.ignore()

當使用者cin錯誤的資料時，交由cin來判斷

```cpp=
int num;
while (!(cin >> num)){
    cout << "輸入並非 Interger." << endl;
    
    // 此處讀取cin的狀態，為1~4
    cout << cin.rdstate() << endl;
    // 清楚cin錯誤的狀態
    cin.clear();
    // 此處讀取清除後cin的狀態，為0
    cout << cin.rdstate() << endl;
    // 此處清除掉在input stream buffer裡面無法處理的資料
    // 第一個參數是要清除多少資料, 第二個參數是要碰到哪個字元才停止清楚
    // 下面那行的意思是: 從現在起, 以後整行的 input 都丟棄掉
    cin.ignore(numeric_limits<streamsize>::max(), '\n');
}
```

*    補充說明
[numeric_limits](https://en.cppreference.com/w/cpp/types/numeric_limits)
```
// 可算出該資料型態最大可能值
numeric_limits<datatype>::max();

```
###### tags: `C++`

