# 🅿️ 商業大樓停車位預約系統 - Backend

本專案為公司內部停車位預約系統的後端 API，提供車位申請、審核、用戶資料管理等功能，搭配前端介面（Vue）使用。

🔗 前端專案：[ParkFlow_Frontend](https://github.com/StevenShih-0402/ParkFlow_Frontend)

---

## 📌 專案緣由

由於公司大樓尚無固定配額車位，需員工每週向設備管理員 (FM) 提出申請，由 FM 向管委會申請臨時車位後，再由 FM 回信給申請者。為減少紙本往來與溝通成本，主管提出需求並主導開發方向，我與同事協作開發本系統，使車位申請流程更自動化與透明。

- 📁 版控 & Code Review：Git / GitHub  
- 🗂 專案管理：Trello  

---

## ⚙️ 技術資訊

| 技術 | 說明 |
|------|------|
| JDK | 17 |
| 框架 | Spring Boot 3.4.4 |
| 建構工具 | Gradle |
| 資料庫 | Oracle 19c |
| SQL 操作 | Oracle SQL Developer |
| 認證機制 | JWT (JSON Web Token) |
| IDE | IntelliJ IDEA |
| 預設啟動位址 | `http://localhost:8080` |

---

## 🧱 系統架構

後端採標準分層架構設計：

```
├── controller         # 展示層，負責處理 API 請求
├── service            # 業務層，處理商業邏輯
├── repository         # DAO 層，連接資料庫
├── entity             # ORM 映射資料表
├── dto                # 資料傳輸物件
├── config             # Spring 設定（CORS、Swagger 等）
├── enums              # 枚舉類型定義
├── exception          # 自訂例外處理
├── exceptionHandler   # 自訂例外處理
├── response           # 統一回傳格式封裝
├── validTag           # 自訂驗證規則
├── jwtToken           # JWT 驗證處理
```

---

## 🧩 資料庫設計

### 📌 ERD（實體關聯圖）

![ERD](https://github.com/user-attachments/assets/16443286-fe59-4d91-bfd6-440305a12dab)

### 📋 補充說明

- 所有主鍵欄位皆透過序列（如：`SEQ_USERS`）與觸發器（如：`TRG_USERS_ID`）自動產生。
- 與 Oracle DB 搭配 JPA 使用，確保資料一致性與交易安全。

---

## 👥 使用者角色說明

| 角色 | 功能權限 |
|------|----------|
| 一般使用者 (USER) | - 發送停車位申請<br>- 查詢個人申請狀態 |
| 管理員 (FM) | - 審核所有申請<br>- 設定每週可預約車位數 |

---

## 🧪 API 文件

共開發 11 支 RESTful API，並提供 Postman 測試匯入檔案：

📥 [下載 Postman 測試檔案](https://drive.google.com/uc?export=download&id=1if2vV0Rjo-tpyJgJOWMHa9fzgrnSDJBs)

| 類別 | 說明 | 路徑 |
|------|------|------|
| 使用者管理 | 註冊 | `POST /v1/user/create` |
|  | 登入 | `POST /v1/user/login` |
|  | 登出 | `POST /v1/user/logout` |
|  | 查詢個人資料 | `GET /v1/user/query` |
|  | 更新個人資料 | `PUT /v1/user/update` |
| 車位申請 | 送出申請 | `POST /v1/parking/create-parking-request` |
|  | 審核申請 | `PUT /v1/parking/update-parking-request` |
|  | 查詢申請紀錄 (USER) | `GET /v1/parking/query-user-parking-request` |
|  | 查詢所有申請 (FM) | `GET /v1/parking/query-fm-parking-request` |
| 車位設定 | 新增預約配額 | `POST /v1/parking/create-parking-quota` |
|  | 更新預約配額 | `PUT /v1/parking/update-parking-quota` |

---

## 📂 專案啟動方式

```bash
# 匯入專案至 IntelliJ IDEA
# 確認 Gradle 設定正確，並下載依賴套件 (理論上 IntelliJ 會自動執行)

# 編譯並啟動專案
./gradlew bootRun，或是從右側 Gradle 版面點擊 bootRun。
```

🔐 預設埠號為 `8080`，如需調整請修改 `application.properties` 設定。

---

## 📗 單元測試

合計 44 支，在每支 API 的 Controller 與 Service 撰寫一正一負之測試。

![image](https://github.com/user-attachments/assets/648b1161-cd02-40cc-87c2-c71a4c346d8c)
