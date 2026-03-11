$DOCKER_USER = "2929028544"
$SERVICES = @("blog-gateway", "blog-user-service", "blog-article-service", "blog-comment-service", "blog-notify-service", "blog-manage-service")

docker login

foreach ($SERVICE in $SERVICES) {
    Write-Host " 正在构建并推送: $SERVICE ..." -ForegroundColor Green
    $IMAGE_TAG = "${DOCKER_USER}/${SERVICE}:latest"
    Write-Host "   Tag: $IMAGE_TAG" -ForegroundColor Cyan
    docker build --build-arg SERVICE_NAME=$SERVICE -t $IMAGE_TAG .
    if ($LASTEXITCODE -eq 0) {
        docker push $IMAGE_TAG
        Write-Host " $SERVICE 已推送成功" -ForegroundColor Green
    } else {
        Write-Host " $SERVICE 构建失败（请把错误贴给我）" -ForegroundColor Red
    }
}
Write-Host " 全部 6 个微服务镜像推送完成！现在可以去服务器部署了" -ForegroundColor Cyan
