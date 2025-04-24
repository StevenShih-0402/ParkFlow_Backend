>Frontend：https://github.com/StevenShih-0402/ParkFlow_Frontend

Note:
- IntelliJ IDEA + Gradle
- JDK 17
- Spring Boot 3.4.4
- Oracle 19c + SQL Developer
- localhost:8080
- 身分驗證：JWT

ERD:

![image (16)](https://github.com/user-attachments/assets/16443286-fe59-4d91-bfd6-440305a12dab)

每張資料表有設定序列(例如：SEQ_USERS) 與觸發器(例如：TRG_USERS_ID) 自動新增 id

系統架構：
1. 展示層(Controller)
2. 業務層(Service)
3. 資料層(DAO、Repository、Entity)
4. 共用層(DTO、Utils、Exception、Response、Enums、Config、Valid、JwtToken)

- 開發緣由

  由於當時公司在大樓尚未有固定配額的停車位，因此員工如果要開車上班，就需要每週向大樓提交臨時車位申請。為了簡化預約流程，主管決定要開發一套供辦公室內部申請車位的系統，由主管提出需求，我和同事們協作開發此系統，讓辦公室同仁可以清楚確認每週申請狀況，減少寫信、寄信等書面往來的時間成本。
  - 版控 & Code Review：Git/GitHub
  - 專案管理：Trello
  
- 使用情境

  系統會有申請停車位的一般使用者 (USER) 與審核申請的管理員 (FM)，USER 登入系統後，可以發送車位申請給 FM，FM 登入系統後就會去審核這些申請，回傳給 USER 他們這週的車位號碼，同時也要在系統設定每週能提供預約的車位數量。
  ![image](https://github.com/user-attachments/assets/510d414c-057f-4196-8a6a-c150a72cc6b2)

- API

   合計開發 11 支 API，附上 [Postman 測試檔案](https://drive.google.com/uc?export=download&id=1if2vV0Rjo-tpyJgJOWMHa9fzgrnSDJBs)
  1. 使用者註冊(`/v1/user/create`)
  2. 使用者登入(`/v1/user/login`)
  3. 使用者登出(`/v1/user/logout`)
  4. 查詢使用者資料(`/v1/user/query`)
  5. 更新使用者資料(`/v1/user/update`)
  6. 申請停車位(`/v1/parking/create-parking-request`)
  7. 審核停車位(`/v1/parking/update-parking-request`)
  8. 取得一般使用者申請記錄(`/v1/parking/query-user-parking-request`)
  9. FM 取得所有使用者申請記錄(`/v1/parking/query-fm-parking-request`)
  10. 新增一週停車數量(`/v1/parking/create-parking-quota`)
  11. 更新一週停車數量(`/v1/parking/update-parking-quota`)
