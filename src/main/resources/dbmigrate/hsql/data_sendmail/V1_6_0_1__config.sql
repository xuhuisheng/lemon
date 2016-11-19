

UPDATE SENDMAIL_CONFIG SET HOST='mail.mossle.com',PORT=465,USERNAME='lingo@mossle.com',PASSWORD='lingo2mossle',SMTP_STARTTLS=0,SMTP_SSL=1,DEFAULT_FROM='lingo@mossle.com' WHERE ID=1;

UPDATE SENDMAIL_TEMPLATE SET SENDER='测试<lingo@mossle.com>',RECEIVER='lingo<lingo@mossle.com>,vivian<vivian@mossle.com>' WHERE ID=1;
UPDATE SENDMAIL_TEMPLATE SET SENDER='测试<lingo@mossle.com>',RECEIVER='演示<lingo@mossle.com>' WHERE ID=2;

