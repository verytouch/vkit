### 1. 仓库

```
git config --global user.mail "aa@aa.com"
git config --global user.name "aaaa"
git config --global user.name

git clone url
git init
git remote remove origin
git remote add origin url
git remote set-url origin newUr
git remote get-url origin

git add aaa.txt
git commit -m "add aaa.txt"
git pull
git push
git push origin v1.0

git diff --shortstat "@{0 day ago}"
```

### 2. 分支
	git branch -avv
	git branch -d test
	git branch --set-upstream-to=origin/v1.0
	git branch --unset-upstream	
	
	git checkout master
	git checkout -b v1.0
	git checkout -b v1.0 cid
	git checkout -b v1.0 origin/v1.0
	
	git push origin v1
	git push origin v1:v1
	git push origin :v1
	git push -u origin v1
	
	git merge dev
	git rebase dev
	git cherry-pick cid
	
	git merge --squash dev
	git commit -m "合并dev多次提交"

### 3. 回退

```
git log --oneline --graph
git reflog
git checkout -- f
git reset --hard HEAD^
git reset --soft HEAD~2
git revert cid
```

### 4. 标签

```
git tag -a v1.0
git tag -a v1.0 cid
git tag -a v1.0 cid -m "add tag v1.0"
git tag
git show v1.0
git tag -d v1.0
git push origin :refs/tags/v1.0
git push origin v1.0
```

### 5. 暂存	

```
git stash
git stash save "aaa"
git stash list
git stash show
git stash apply
git stash pop
git stash drop stash@{1}
git stash clear
```

### 6.  规范

> 注释
>
> * feat 新功能
> * fix 改bug
> * refactor 重构
> * test 测试
> * docs 文档
> * style 格式
> * chore 构建

> 分支
>
> * master 主分支，每个版本在对应commit处打tag
> * feature 功能分支
> * hotfix 线上bug修复分支
> * 其他分支如test、dev分支按需规定
