一大早发现周末的代码commit之后没有push。按照之前的常规操作，采用用户名+密码的方式，通过https的方式push代码。结果出现如下错误：
```
remote: Support for password authentication was removed on August 13, 2021. Please use a personal access token instead.
remote: Please see https://github.blog/2020-12-15-token-authentication-requirements-for-git-operations/ for more information.
fatal: unable to access 'https://github.com/zhoulujun/algorithm.git/': The requested URL returned error: 403
```
查看官方的解释：https://github.blog/changelog/2021-08-12-git-password-authentication-is-shutting-down/
```
As previously announced, starting on August 13, 2021, at 09:00 PST, we will no longer accept account passwords when authenticating Git operations on GitHub.com. Instead, token-based authentication (for example, personal access, OAuth, SSH Key, or GitHub App installation token) will be required for all authenticated Git operations.

Please refer to this blog post for instructions on what you need to do to continue using git operations securely.

Removal
August 13, 2021, at 09:00 PST
```
自2021年8月13日以后，以用户名+密码的方式将不被支持。现在可以支持的方式有： OAuth、SSH Key或者GitHub App installation token) 。
个人对比了下，最便捷的方式就是采用ssh Key的方式了。

# 1.生成ssh密钥
ssh key的思路是，在本地，通过sshkeygan的方式，产生一组用于加密的RSA公私钥，之后，git在提交代码的过程中，通过本地的私钥加密，之后将加密数据传递到github服务器。
而对于github服务端，我们则需要将刚产生的公钥配置在github中，用于对数据的解密。这样就实现了ssh的方式提交代码。
第一步是产生ssh的rsakey,过程如下:
```
#打开git的shell窗口，先设置github账号的username
Administrator@DESKTOP-HR38DGU MINGW64 /c/Program Files (x86)
$ git config --global user.name "haibo-duan"
#设置email
Administrator@DESKTOP-HR38DGU MINGW64 /c/Program Files (x86)
$ git config --global user.email "dhaibo1986@live.cn"
# 需要在D盘新建一个文件夹gitrep来存放生成的key文件
Administrator@DESKTOP-HR38DGU MINGW64 /c/Program Files (x86)
$ cd D:

Administrator@DESKTOP-HR38DGU MINGW64 /d
$ mkdir gitrep

Administrator@DESKTOP-HR38DGU MINGW64 /d
$ cd gitrep/

#执行init 
Administrator@DESKTOP-HR38DGU MINGW64 /d/gitrep
$ git init
Initialized empty Git repository in D:/gitrep/.git/
#采用ssh-keygam -t -rsa产生密钥
Administrator@DESKTOP-HR38DGU MINGW64 /d/gitrep (master)
$ ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/c/Users/Administrator/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /c/Users/Administrator/.ssh/id_rsa.
Your public key has been saved in /c/Users/Administrator/.ssh/id_rsa.pub.
The key fingerprint is:
SHA256:********riiz4hYX5kGEpW6bLn8fkMwLvXbotBzflIM Administrator@DESKTOP-HR38DGU
The key's randomart image is:
+---[RSA 2048]----+
|  +o             |
| ...             |
| o..             |
|o .B..           |
|.++.O   S        |
|+o++ * o .       |
|oo+.O E +        |
|B+.*.* + .       |
|XB..+.o .        |
+----[SHA256]-----+

```
这样就生成了用于加密的公私钥文件。
产生的公私钥文件将放置在C:\Users\Administrator\.ssh目录。如果在linux系统，这个位置就在用户所在的根目录中。
通过cat可以查看：
```
Administrator@DESKTOP-HR38DGU MINGW64 /d/gitrep (master)
$ cat ~/.ssh/id_rsa.pub
ssh-rsa ********aC1yc2EAAAADAQABAAABAQDOsmo+mM3J6s9t07AyYWA5qwqPeMrkcKWTxVk7gyF1JY423txCOGhTNVGXLk59J4hyBJ67u9TAQpvatQpx0vk/8LUUSHlcDh4CU6U6S0gHtCi8dptG3q1SZwvvifDg6udj+fF4pR5pD0YN1DkQg22zNJzlYlAqu5sjW02+GeOxDKoyb+bIGYBxwIfcByb2fH9nahnyvfW8sCjoS0BzRRCh9HP3sKY6cLxajiNo6/e9bjVjocQkE9C7zOF7PNG8+AM3KMgf9qx2Cyvp7vimFDYjKuF3HSeNyhLEKQLyF5XDFBAI3KJ86G5yhpaljsSdI4y/cWVpzDU0+1ZUpRIOF3/z Administrator@DESKTOP-HR38DGU
```
这样产生的公钥，需要复制出来，在github上进行配置。

# 2.github配置

在github的 Setting部分：
![github sshkey add](../../images/github%20sshkey%20add.png)
将上述的公钥部分copy到此处保存。配置完成之后如下图：
![github sshkey seting](../../images/github%20sshkey%20seting.png)


# 3.切换本地project的协议
由于本地非常多的项目都是https方式进行提交，如果全部都通过ssh的方式重新clone之后再import,工作量非常大。可以采用命令行进行切换：
```
# 在本地的一个项目中打开git-shell，查看版本信息
Administrator@DESKTOP-HR38DGU MINGW64 /d/workspace-mashibing/geektime-study (main)
$ git remote -v
origin  https://github.com/haibo-duan/geektime-study.git (fetch)
origin  https://github.com/haibo-duan/geektime-study.git (push)
#切换
Administrator@DESKTOP-HR38DGU MINGW64 /d/workspace-mashibing/geektime-study (main)
$ git remote set-url origin git@github.com:haibo-duan/geektime-study.git
#再次查看
Administrator@DESKTOP-HR38DGU MINGW64 /d/workspace-mashibing/geektime-study (main)
$ git remote -v
origin  git@github.com:haibo-duan/geektime-study.git (fetch)
origin  git@github.com:haibo-duan/geektime-study.git (push)

```

通过git remote set-url origin就能很好的切换。
需要注意的是，第一次提交会出现提示：
```
The authenticity of host 'github.com' can't be established. Are you sure you want to continue connecting (yes/no/[fingerprint])?..
```
确认无误则选择yes。

这样以后在idea中提交代码则不再需要输入密码了。



