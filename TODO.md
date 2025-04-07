# LearningTracker 后端开发待办事项

## 核心功能模块

### 1. 学习目标(Goals)模块

- [ ] **实体类**: `Goal.java`
  - 字段: id, 标题, 描述, 截止日期, 完成状态, 优先级, 关联用户 ID 等
- [ ] **Repository**: `GoalRepository.java`
- [ ] **Service**: `GoalService.java` 和 `GoalServiceImpl.java`
- [ ] **Controller**: `GoalController.java` - REST API 端点
- [ ] **DTO**: 请求/响应数据传输对象

### 2. 学习任务(Tasks)模块

- [ ] **实体类**: `Task.java`
  - 字段: id, 标题, 描述, 截止日期, 状态, 关联目标 ID, 预计时间等
- [ ] **Repository**: `TaskRepository.java`
- [ ] **Service**: `TaskService.java` 和 `TaskServiceImpl.java`
- [ ] **Controller**: `TaskController.java`
- [ ] **DTO**: 任务相关 DTO

### 3. 学习资源(Resources)模块

- [ ] **实体类**: `Resource.java`
  - 字段: id, 标题, 描述, 资源类型, URL, 文件路径, 标签, 关联用户 ID 等
- [ ] **Repository**: `ResourceRepository.java`
- [ ] **Service**: `ResourceService.java` 和实现类
- [ ] **Controller**: `ResourceController.java`
- [ ] **DTO**: 资源相关 DTO

### 4. 学习进度(Progress)跟踪模块

- [ ] **实体类**: `StudySession.java`/`Progress.java`
  - 字段: id, 用户 ID, 学习时长, 学习日期, 关联任务/目标 ID 等
- [ ] **Repository**: `ProgressRepository.java`
- [ ] **Service**: `ProgressService.java` 和实现类
- [ ] **Controller**: `ProgressController.java`
- [ ] **统计分析功能**: 学习时间统计、完成率统计等

## 增强功能模块

### 5. 成就(Achievements)系统

- [ ] **实体类**:
  - `Achievement.java` - 成就定义
  - `UserAchievement.java` - 用户获得的成就关联表
- [ ] **Repository**: 相关 Repository 接口
- [ ] **Service**: 成就解锁逻辑和业务处理
- [ ] **Controller**: `AchievementController.java`

### 6. 课程(Courses)模块

- [ ] **实体类**:
  - `Course.java` - 课程基本信息
  - `Chapter.java` - 课程章节
  - `Lesson.java` - 具体课时
- [ ] **Repository**: 相关数据访问接口
- [ ] **Service**: 课程管理和学习进度跟踪
- [ ] **Controller**: `CourseController.java`

### 7. 社区(Community)功能

- [ ] **实体类**:
  - `Post.java` - 讨论贴
  - `Comment.java` - 评论
  - `Like.java` - 点赞
- [ ] **Repository**: 相关数据访问接口
- [ ] **Service**: 社区互动业务逻辑
- [ ] **Controller**: `CommunityController.java`

## 系统功能完善

### 8. 身份和权限管理

- [ ] **实体类**: `Role.java` 和 `UserRole.java`
- [ ] **扩展安全配置**: 基于角色的访问控制(RBAC)
- [ ] **Admin 管理功能**: 用户管理、数据管理等

### 9. 通知系统

- [ ] **实体类**: `Notification.java`
- [ ] **Service**: 通知发送和管理
- [ ] **Controller**: `NotificationController.java`
- [ ] **推送机制**: 邮件通知、站内消息等

### 10. 文件上传功能

- [ ] **Service**: `FileStorageService.java`
- [ ] **Controller**: `FileController.java`
- [ ] **配置**: 文件存储路径、大小限制等

### 11. 搜索功能

- [ ] **全文搜索**: ElasticSearch 集成或数据库搜索
- [ ] **高级筛选**: 复杂查询条件支持
- [ ] **API 端点**: 搜索相关接口

### 12. API 文档和系统监控

- [ ] **完善 Swagger/Knife4j 配置**
- [ ] **Actuator 配置**: 系统监控端点
- [ ] **日志增强**: 关键操作日志记录

### 13. 数据分析和报表系统

- [ ] **Service**: 数据统计和分析服务
- [ ] **Controller**: 报表生成接口
- [ ] **导出功能**: PDF/Excel 报表导出

### 14. 设置模块

- [ ] **实体类**: `UserSetting.java`
- [ ] **Service 和 Controller**: 用户偏好设置管理

## 其他技术改进

- [ ] **缓存机制**: Redis 集成
- [ ] **异步处理**: 消息队列集成
- [ ] **批量处理**: 大数据量处理优化
- [ ] **定时任务**: 进度通知、数据清理等
- [ ] **单元测试**: 提高测试覆盖率
- [ ] **CI/CD 配置**: 自动化部署

## 优先级排序建议

1. 学习目标和任务管理 (1-2)
2. 学习资源管理 (3)
3. 进度跟踪 (4)
4. 成就系统 (5)
5. 课程模块 (6)
6. 社区功能 (7)
7. 其他辅助功能 (8-14)
