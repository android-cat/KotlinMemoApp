# KotlinMemoApp

Android メモアプリ - Kotlin + Room Database

## 概要
AndroidStudioを用いたKotlinで作るネイティブのメモアプリです。

## プロダクトイメージ
シンプルなUI

## アーキテクチャ
- MVVM (Model-View-ViewModel)
- Room Database (ローカルストレージ)
- REST API対応の設計（将来的な拡張に対応）

## 使用技術
- **言語**: Kotlin
- **UI**: XML Layouts
- **データベース**: Room (SQLite)
- **非同期処理**: Kotlin Coroutines
- **アーキテクチャコンポーネント**: 
  - ViewModel
  - LiveData
  - Repository Pattern

## 機能
1. ✅ メモにタイトルをつけられる
2. ✅ メモを記入できる
3. ✅ メモを保存できる
4. ✅ メモをフォルダでまとめられる
5. ✅ フォルダに名前がつけられる
6. ✅ メモの内容で検索できる

## プロジェクト構成

```
app/src/main/
├── java/com/example/kotlinmemoapp/
│   ├── data/
│   │   ├── database/
│   │   │   ├── FolderDao.kt         # フォルダのDAO
│   │   │   ├── MemoDao.kt           # メモのDAO
│   │   │   └── MemoDatabase.kt      # Room Database
│   │   ├── model/
│   │   │   ├── Folder.kt            # フォルダエンティティ
│   │   │   └── Memo.kt              # メモエンティティ
│   │   └── repository/
│   │       ├── FolderRepository.kt  # フォルダリポジトリ
│   │       └── MemoRepository.kt    # メモリポジトリ
│   ├── ui/
│   │   ├── FolderActivity.kt        # フォルダ管理画面
│   │   ├── FolderAdapter.kt         # フォルダリストアダプター
│   │   ├── FolderDialogHelper.kt    # フォルダダイアログヘルパー
│   │   ├── MainActivity.kt          # メイン画面
│   │   ├── MemoAdapter.kt           # メモリストアダプター
│   │   └── MemoEditActivity.kt      # メモ編集画面
│   └── viewmodel/
│       ├── FolderViewModel.kt       # フォルダViewModel
│       └── MemoViewModel.kt         # メモViewModel
└── res/
    ├── layout/                       # XMLレイアウト
    │   ├── activity_main.xml
    │   ├── activity_memo_edit.xml
    │   ├── activity_folder.xml
    │   ├── item_memo.xml
    │   ├── item_folder.xml
    │   └── dialog_folder.xml
    └── values/                       # リソース定義
        ├── strings.xml
        ├── colors.xml
        └── themes.xml
```

## ビルド方法

### 必要環境
- Android Studio Arctic Fox (2020.3.1) 以上
- JDK 17
- Android SDK (API Level 24以上)
- Gradle 8.1

### ビルド手順
1. プロジェクトをクローン
```bash
git clone https://github.com/android-cat/KotlinMemoApp.git
```

2. Android Studioでプロジェクトを開く

3. Gradleの同期を実行

4. エミュレーターまたは実機でアプリを実行

## 主な画面
- **メイン画面**: メモ一覧とフォルダ一覧をタブで切り替え、検索機能
- **メモ編集画面**: タイトル、内容、フォルダの選択
- **フォルダ管理**: フォルダの作成、編集、削除

## データベース設計

### Memoテーブル
- id: Long (Primary Key)
- title: String
- content: String
- folderId: Long? (Foreign Key)
- createdAt: Long
- updatedAt: Long

### Folderテーブル
- id: Long (Primary Key)
- name: String
- createdAt: Long

## 今後の拡張予定
- REST API連携機能
- クラウド同期
- メモの共有機能
- リッチテキスト編集
- 添付ファイルのサポート
