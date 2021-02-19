## 文字转语音

[![license](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg?style=flat)](https://github.com/IOMurphy/Clipboard2Voice/blob/master/LICENSE)
[![Release Version](https://img.shields.io/badge/release-v1.0.1-red.svg)](https://github.com/IOMurphy/Clipboard2Voice/releases)

主要提供给文盲使用，只需复制文字，app就会自动转语音，并且读出来（based on Google TTS Engine)。

## 使用教程

1. 请到[![Release Version](https://img.shields.io/badge/release-v1.0.1-red.svg)](https://github.com/IOMurphy/Clipboard2Voice/releases)下载软件。其中app.apk是软件本体，GoogleTTS.apk是谷歌语音转文字插件，只需要下载这两个文件就可以了，
2. 在安装完软件之后，在系统设置中将谷歌TTS设置为默认语音转文字插件，然后把该给的权限都给上。
3. 设置语速、语调和语言。
4. 打开其他app，选中文字，然后长按，点击复制，然后软件会自动读出对应的语音。（安卓10以上需要在app才能获取剪切板内容，见【已知问题】）

## 自启动

开机自启动，启动条件如下（赋予权限的前提下）：

1. 开机
2. 网络状态改变自启动

## 已知问题

1. 安卓10除了输入法，其他第三方应用都不能在后台获取剪切板内容。然后解决方案参考[这里](https://blog.csdn.net/qq_37891456/article/details/103383116)。但是Activity也会内存泄漏，本来他们用的手机就比较卡，所以暂时不采用！
2. 暂时没有加入签名，所以可能不同版本之间需要卸载才能安装.
3. [bug] TextToSpeech 在return之后没有，所以应用启动后的第一次调用无效。

## License

本软件使用[Apache License 2.0. ](https://github.com/IOMurphy/Clipboard2Voice/blob/master/LICENSE)