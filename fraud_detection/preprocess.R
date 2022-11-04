######################## 
# loading the data set #
########################
set.seed(1223)

library(data.table)

train <- fread("train.csv", sep=",", header=TRUE)
test <- fread("test.csv", sep=",", header=TRUE)

ids <- sample(1:nrow(train), 0.1*nrow(train)) # select a sample (10%) of the entire data set to allow a faster exploration of the data
ds <- train[ids,] # the working subset

tids <- sample(1:nrow(test), 0.1*nrow(test)) # select a sample (10%) of the entire data set to allow a faster exploration of the data
tds <- test[tids,] # the working subset

######################
# Exploring the data #
######################
library("dplyr")
library("lubridate")
library("ggplot2")

###############
# Existe campos que não têm valor, vamos substituir por <NA>
ds <- subset(ds, select=-c(id_07, id_08, id_21, id_22, id_23, id_24, id_25, id_26, id_27))
ds <- subset(ds, select=-c(47:55))
ds[ds==''] <- NA

# summary
summ <- summary(ds)

# Missing data
data_na <- (colSums(is.na(ds)) / nrow(ds)) * 100
# algumas colunas têm falta de valor em mais de 99%, o que as torna dispensáveis
# id_[07, 08, 21, 22, 23, 24, 25, 26, 27] 
## WE CAN ERASE THESE COLUMNS
#ds <- subset(ds, select=-c(id_07, id_08, id_21, id_22, id_23, id_24, id_25, id_26, id_27))

#######################
#isFraud
ggplot(ds, aes(x=isFraud)) + geom_bar() + 
  scale_x_continuous(name="isFraud", breaks=c(0,1)) + 
  geom_text(aes(label=after_stat(sprintf("%.2f%%", prop*100))), stat='count', vjust=-0.2)
  
  
# TransactionDT
# timedelta from a given reference datetime (not an actual timestamp)
# let's say TransactionDT is the seconds passed from day0 
# invented by us "january first 2020" can be a parameter in reporting script
# adding columns hour and mday to represent hour and day of month of transaction
day0 <- dmy_hms("1/1/2020 00:00:00 GMT")
ds <- mutate(ds, hour=hour(day0+seconds(TransactionDT)))
ds <- mutate(ds, mday=mday(day0+seconds(TransactionDT)))




# ploting transactions per mday and hour
ggplot(ds) + aes(x=mday) + geom_bar() + scale_x_continuous(breaks = seq(1,31,1))  
md <- group_by(ds, mday) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(mday, fraud, notfraud, n)
ggplot(md) + aes(x=mday, y=(fraud/n)*100) + geom_line() +
  scale_x_continuous(name="Day of month", breaks = seq(0,31,1)) +
  labs(y="Percentage of fraudulent transactions")


ggplot(ds) + aes(x=hour) + geom_bar() + scale_x_continuous(breaks = seq(0,23,1))
h <- group_by(ds, hour) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(hour, fraud, notfraud, n)
ggplot(h) + aes(x=hour, y=(fraud/n)*100) + geom_line() +
  scale_x_continuous(name="Hour", breaks = seq(0,23,1)) +
  labs(y="Percentage of fraudulent transactions")
# OBSERVATIONS: we can see that there's not a lot of transactions between 5-12


####################
# TransactionAmt
ggplot(ds, aes(x=TransactionDT, y=TransactionAmt, fill=isFraud==1)) + geom_point(alpha=0.4) + 
  labs(x="TransactionAmt") 

ggplot(tds, aes(x=TransactionDT, y=TransactionAmt)) + geom_point(alpha=0.4) + 
  labs(x="TransactionAmt") 


filter(ds, TransactionAmt>3000) %>% filter(isFraud==1) %>% select(TransactionAmt, isFraud)
# obs. only 1 case is fraud
# drop cases where TransactionAmt>1000
ds <- filter(ds, TransactionAmt<=3000)

ggplot(ds, aes(x=TransactionAmt, fill=isFraud==1)) + geom_density(alpha=0.4) + 
  labs(x="TransactionAmt") + scale_fill_discrete(name="isFraud")


####################
# ProductCD
ggplot(ds)  + aes(x=ProductCD) + coord_flip() + geom_bar() 

product <- group_by(ds, ProductCD) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(ProductCD, fraud, notfraud, n)
product <- unique(product)

ggplot(product) + aes(x=ProductCD, y=(fraud/notfraud)) + coord_flip() + geom_col() + 
  scale_y_continuous(name="Percentage of fraud transactions", breaks = seq(0,0.15,0.01)) 
# OBSERVACOES: existe muitas fraudes no ProductCD == C > S > H > R > W


# Card1-6
select(ds, c(6:11)) %>% summary()
# card4 e card6 são strings
# Não há NA's no card1
# o range do card1 é muito superior em relação ao card2, card3 e card5
# card2, card3 e card5 têm uma pequena percentagem de NA's

# valores em card1 e card2 estão bem espalhados, ao contrário de card3
ggplot(ds) + aes(x=card1) + geom_density(fill='blue', alpha=0.4)
ggplot(ds) + aes(x=card2) + geom_density(fill='blue', alpha=0.4)
ggplot(ds) + aes(x=card3) + geom_density(fill='blue', alpha=0.4)
ggplot(ds) + aes(x=card5) + geom_density(fill='blue', alpha=0.4)

# differentes valores em card4
ggplot(ds) + aes(x=card4) + geom_bar()

card <- group_by(ds, card4) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(card4, fraud, notfraud, n)
card <- unique(card)
ggplot(card) + aes(x=card4, y=(fraud/notfraud)) + coord_flip() + geom_col() + 
  scale_y_continuous(name="Percentage of fraud in card4", breaks = seq(0,0.4,0.05)) 

# differentes valores em card6
ggplot(ds) + aes(x=card6) + geom_bar()

card <- group_by(ds, card6) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(card6, fraud, notfraud, n)
card <- unique(card)
ggplot(card) + aes(x=card6, y=(fraud/notfraud)) + coord_flip() + geom_col() + 
  scale_y_continuous(name="Percentage of fraud in card6", breaks = seq(0,0.4,0.05)) 

# criar card a partir de card1-6
ds <- ds %>% mutate(card=group_indices(., card1, card2, card3, card4, card5, card6))





# Do something with card
ggplot(ds) + aes(x=card, fill=isFraud==1) + geom_density(alpha=0.4)


# addr1 addr2
# different values
diff_values <- length(unique(ds$addr1))
diff_values

missing <- ( (ds %>% summarise(sum(is.na(addr1))) ) / nrow(ds) ) * 100
missing

ggplot(ds) + aes(x=addr1, fill=isFraud==1) + geom_density(alpha=0.4) + 
  scale_x_continuous(breaks = seq(90,550,30)) +
  scale_fill_discrete(name="Is fraud?")

diff_values <- length(unique(ds$addr2))
diff_values

missing <- ( (ds %>% summarise(sum(is.na(addr2))) ) / nrow(ds) ) * 100
missing

ggplot(ds) + aes(x=addr2, fill=isFraud==1) + geom_density() + 
  scale_x_continuous(breaks = seq(0,100,5)) + 
  scale_fill_discrete(name="Is fraud?")

# dis1 dist2
diff_values <- length(unique(ds$dist1))
diff_values

missing <- ( (ds %>% summarise(sum(is.na(dist1))) ) / nrow(ds) ) * 100
missing

ggplot(ds) + aes(x=TransactionDT, y=dist1) + geom_point()
filter(ds, dist1>=2000) %>% filter(isFraud==1) %>% select(dist1) # nenhum caso
#drop dist1>=2000
#ds <- filter(ds, dist1<2000)

diff_values <- length(unique(ds$dist2))
diff_values

missing <- ( (ds %>% summarise(sum(is.na(dist2))) ) / nrow(ds) ) * 100
missing

ggplot(ds) + aes(x=TransactionDT, y=dist2) + geom_point()
filter(ds, dist1>=2000) %>% filter(isFraud==1) %>% select(dist1) # nenhum caso
#drop dist1>=2000
#ds <- filter(ds, dist1<2000)


ds %>% summarise(avg.dist1=mean(dist1, na.rm=TRUE),
                 med.dist1=median(dist1, na.rm=TRUE),
                 var.dist1=var(dist1, na.rm=TRUE),
                 sd.dist1=sd(dist1, na.rm=TRUE),
                 iqr.dist1=IQR(dist1, na.rm=TRUE)
                 )

ds %>% summarise(avg.dist2=mean(dist2, na.rm=TRUE),
                 med.dist2=median(dist2, na.rm=TRUE),
                 var.dist2=var(dist2, na.rm=TRUE),
                 sd.dist2=sd(dist2, na.rm=TRUE),
                 iqr.dist2=IQR(dist2, na.rm=TRUE)
)

# P_emaildomain e R_emaildomain
# P_emaildomain with no value
ggplot(ds) + aes(x=is.na(P_emaildomain)) + geom_bar() + labs(x="Missing value in P_emaildomain")

# P_emaildomain diferentes valores
ggplot(ds) + aes(x=P_emaildomain) + coord_flip() + geom_bar(stat='count')

Pemail <- group_by(ds, P_emaildomain) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(P_emaildomain, fraud, notfraud, n)
Pemail <- unique(Pemail)
ggplot(Pemail) + aes(x=P_emaildomain, y=(fraud/notfraud)) + coord_flip() + geom_col() + 
  scale_y_continuous(name="Percentage of fraud in P_emaildomain", breaks = seq(0,0.4,0.05)) 


# R_emaildomain NA's 
ggplot(ds) + aes(x=is.na(R_emaildomain)) + geom_bar() + labs(x="Missing value in R_emaildomain")

# R_emaildomain diferentes valores
ggplot(ds) + aes(x=R_emaildomain) + coord_flip() + geom_bar(stat='count')

Remail <- group_by(ds, R_emaildomain) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(ds)) %>% select(R_emaildomain, fraud, notfraud, n)
Remail <- unique(Remail)
ggplot(Remail) + aes(x=R_emaildomain, y=(fraud/notfraud)) + coord_flip() + geom_col() + 
  scale_y_continuous(name="Percentage of fraud in R_emaildomain", breaks = seq(0,1.2,0.1)) 


# C1-C14 counting
counting1 <- ds %>% summarise( col = "C1",
                 diff=length(unique(ds$C1)),
                 max=max(C1),
                 min=min(C1),
                 avg=mean(C1, na.rm=TRUE),
                 med=median(C1, na.rm=TRUE),
                 var=var(C1, na.rm=TRUE),
                 sd=sd(C1, na.rm=TRUE)
)
counting2 <- ds %>% summarise( col = "C2",
                              diff=length(unique(ds$C2)),
                              max=max(C2),
                              min=min(C2),
                              avg=mean(C2, na.rm=TRUE),
                              med=median(C2, na.rm=TRUE),
                              var=var(C2, na.rm=TRUE),
                              sd=sd(C2, na.rm=TRUE)
)
counting3 <- ds %>% summarise( col = "C3",
                               diff=length(unique(ds$C3)),
                               max=max(C3),
                               min=min(C3),
                               avg=mean(C3, na.rm=TRUE),
                               med=median(C3, na.rm=TRUE),
                               var=var(C3, na.rm=TRUE),
                               sd=sd(C3, na.rm=TRUE)
)
counting4 <- ds %>% summarise( col = "C4",
                               diff=length(unique(ds$C4)),
                               max=max(C4),
                               min=min(C4),
                               avg=mean(C4, na.rm=TRUE),
                               med=median(C4, na.rm=TRUE),
                               var=var(C4, na.rm=TRUE),
                               sd=sd(C4, na.rm=TRUE)
)
counting5 <- ds %>% summarise( col = "C5",
                               diff=length(unique(ds$C5)),
                               max=max(C5),
                               min=min(C5),
                               avg=mean(C5, na.rm=TRUE),
                               med=median(C5, na.rm=TRUE),
                               var=var(C5, na.rm=TRUE),
                               sd=sd(C5, na.rm=TRUE)
)
counting6 <- ds %>% summarise( col = "C6",
                               diff=length(unique(ds$C6)),
                               max=max(C6),
                               min=min(C6),
                               avg=mean(C6, na.rm=TRUE),
                               med=median(C6, na.rm=TRUE),
                               var=var(C6, na.rm=TRUE),
                               sd=sd(C6, na.rm=TRUE)
)
counting7 <- ds %>% summarise( col = "C7",
                               diff=length(unique(ds$C7)),
                               max=max(C7),
                               min=min(C7),
                               avg=mean(C7, na.rm=TRUE),
                               med=median(C7, na.rm=TRUE),
                               var=var(C7, na.rm=TRUE),
                               sd=sd(C7, na.rm=TRUE)
)
counting8 <- ds %>% summarise( col = "C8",
                               diff=length(unique(ds$C8)),
                               max=max(C8),
                               min=min(C8),
                               avg=mean(C8, na.rm=TRUE),
                               med=median(C8, na.rm=TRUE),
                               var=var(C8, na.rm=TRUE),
                               sd=sd(C8, na.rm=TRUE)
)
counting9 <- ds %>% summarise( col = "C9",
                               diff=length(unique(ds$C9)),
                               max=max(C9),
                               min=min(C9),
                               avg=mean(C9, na.rm=TRUE),
                               med=median(C9, na.rm=TRUE),
                               var=var(C9, na.rm=TRUE),
                               sd=sd(C9, na.rm=TRUE)
)
counting10 <- ds %>% summarise( col = "C10",
                                diff=length(unique(ds$C10)),
                                max=max(C10),
                                min=min(C10),
                                avg=mean(C10, na.rm=TRUE),
                                med=median(C10, na.rm=TRUE),
                                var=var(C10, na.rm=TRUE),
                                sd=sd(C10, na.rm=TRUE)
)
counting11 <- ds %>% summarise( col = "C11",
                                diff=length(unique(ds$C11)),
                                max=max(C11),
                                min=min(C11),
                                avg=mean(C11, na.rm=TRUE),
                                med=median(C11, na.rm=TRUE),
                                var=var(C11, na.rm=TRUE),
                                sd=sd(C11, na.rm=TRUE)
)
counting12 <- ds %>% summarise( col = "C12",
                                diff=length(unique(ds$C12)),
                                max=max(C12),
                                min=min(C12),
                                avg=mean(C12, na.rm=TRUE),
                                med=median(C12, na.rm=TRUE),
                                var=var(C12, na.rm=TRUE),
                                sd=sd(C12, na.rm=TRUE)
)
counting13 <- ds %>% summarise( col = "C13",
                                diff=length(unique(ds$C13)),
                                max=max(C13),
                                min=min(C13),
                                avg=mean(C13, na.rm=TRUE),
                                med=median(C13, na.rm=TRUE),
                                var=var(C13, na.rm=TRUE),
                                sd=sd(C13, na.rm=TRUE)
)
counting14 <- ds %>% summarise( col = "C14",
                                diff=length(unique(ds$C14)),
                                max=max(C14),
                                min=min(C14),
                                avg=mean(C14, na.rm=TRUE),
                                med=median(C14, na.rm=TRUE),
                                var=var(C14, na.rm=TRUE),
                                sd=sd(C14, na.rm=TRUE)
)

counting <- full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(
  counting1, counting2),counting3),counting4),counting5),counting6),counting7),counting8),counting9),counting10),counting11),counting12),counting13),counting14)
counting
  
# D1-D15 time data

ggplot(ds) + aes(x=TransactionDT, y=D1) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D2) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D3) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D4) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D5) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D6) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D7) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D8) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D9) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D10) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D11) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D12) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D13) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D14) + geom_point(alpha=0.4)
ggplot(ds) + aes(x=TransactionDT, y=D15) + geom_point(alpha=0.4)

# normalizing D columns with TransactionDT
ds <- ds %>% mutate(d1n=D1 - (TransactionDT/(24*60*60)), d2n=D2 - (TransactionDT/(24*60*60)),
                    d3n=D3 - (TransactionDT/(24*60*60)),
                    d4n=D4 - (TransactionDT/(24*60*60)),
                    d5n=D5 - (TransactionDT/(24*60*60)),
                    d6n=D6 - (TransactionDT/(24*60*60)),
                    d7n=D7 - (TransactionDT/(24*60*60)),
                    d8n=D8 - (TransactionDT/(24*60*60)),
                    d9n=D9 - (TransactionDT/(24*60*60)),
                    d10n=D10 - (TransactionDT/(24*60*60)),
                    d11n=D11 - (TransactionDT/(24*60*60)),
                    d12n=D12 - (TransactionDT/(24*60*60)),
                    d13n=D13 - (TransactionDT/(24*60*60)),
                    d14n=D14 - (TransactionDT/(24*60*60)),
                    d15n=D15 - (TransactionDT/(24*60*60)))

# summarising new D's
d1 <- ds %>% summarise( col = "d1n",
                        missing = (sum(is.na(d1n)) / nrow(ds))*100,
                        diff=length(unique(ds$d1n)),
                        max=max(d1n, na.rm=TRUE),
                        min=min(d1n, na.rm=TRUE),
                        avg=mean(d1n, na.rm=TRUE),
                        med=median(d1n, na.rm=TRUE),
                        var=var(d1n, na.rm=TRUE),
                        sd=sd(d1n, na.rm=TRUE)
); d1
d2 <- ds %>% summarise( col = "d2n",
                        missing = (sum(is.na(d2n)) / nrow(ds))*100,
                        diff=length(unique(ds$d2n)),
                        max=max(d2n, na.rm=TRUE),
                        min=min(d2n, na.rm=TRUE),
                        avg=mean(d2n, na.rm=TRUE),
                        med=median(d2n, na.rm=TRUE),
                        var=var(d2n, na.rm=TRUE),
                        sd=sd(d2n, na.rm=TRUE)
)
d3 <- ds %>% summarise( col = "d3n",
                        missing = (sum(is.na(d3n)) / nrow(ds))*100,
                        diff=length(unique(ds$d3n)),
                        max=max(d3n, na.rm=TRUE),
                        min=min(d3n, na.rm=TRUE),
                        avg=mean(d3n, na.rm=TRUE),
                        med=median(d3n, na.rm=TRUE),
                        var=var(d3n, na.rm=TRUE),
                        sd=sd(d3n, na.rm=TRUE)
)
d4 <- ds %>% summarise( col = "d4n",
                        missing = (sum(is.na(d4n)) / nrow(ds))*100,
                        diff=length(unique(ds$d4n)),
                        max=max(d4n, na.rm=TRUE),
                        min=min(d4n, na.rm=TRUE),
                        avg=mean(d4n, na.rm=TRUE),
                        med=median(d4n, na.rm=TRUE),
                        var=var(d4n, na.rm=TRUE),
                        sd=sd(d4n, na.rm=TRUE)
)
d5 <- ds %>% summarise( col = "d5n",
                        missing = (sum(is.na(d5n)) / nrow(ds))*100,
                        diff=length(unique(ds$d5n)),
                        max=max(d5n, na.rm=TRUE),
                        min=min(d5n, na.rm=TRUE),
                        avg=mean(d5n, na.rm=TRUE),
                        med=median(d5n, na.rm=TRUE),
                        var=var(d5n, na.rm=TRUE),
                        sd=sd(d5n, na.rm=TRUE)
)
d6 <- ds %>% summarise( col = "d6n",
                        missing = (sum(is.na(d6n)) / nrow(ds))*100,
                        diff=length(unique(ds$d6n)),
                        max=max(d6n, na.rm=TRUE),
                        min=min(d6n, na.rm=TRUE),
                        avg=mean(d6n, na.rm=TRUE),
                        med=median(d6n, na.rm=TRUE),
                        var=var(d6n, na.rm=TRUE),
                        sd=sd(d6n, na.rm=TRUE)
)
d7 <- ds %>% summarise( col = "d7n",
                        missing = (sum(is.na(d7n)) / nrow(ds))*100,
                        diff=length(unique(ds$d7n)),
                        max=max(d7n, na.rm=TRUE),
                        min=min(d7n, na.rm=TRUE),
                        avg=mean(d7n, na.rm=TRUE),
                        med=median(d7n, na.rm=TRUE),
                        var=var(d7n, na.rm=TRUE),
                        sd=sd(d7n, na.rm=TRUE)
)
d8 <- ds %>% summarise( col = "d8n",
                        missing = (sum(is.na(d8n)) / nrow(ds))*100,
                        diff=length(unique(ds$d8n)),
                        max=max(d8n, na.rm=TRUE),
                        min=min(d8n, na.rm=TRUE),
                        avg=mean(d8n, na.rm=TRUE),
                        med=median(d8n, na.rm=TRUE),
                        var=var(d8n, na.rm=TRUE),
                        sd=sd(d8n, na.rm=TRUE)
)
d9 <- ds %>% summarise( col = "d9n",
                        missing = (sum(is.na(d9n)) / nrow(ds))*100,
                        diff=length(unique(ds$d9n)),
                        max=max(d9n, na.rm=TRUE),
                        min=min(d9n, na.rm=TRUE),
                        avg=mean(d9n, na.rm=TRUE),
                        med=median(d9n, na.rm=TRUE),
                        var=var(d9n, na.rm=TRUE),
                        sd=sd(d9n, na.rm=TRUE)
)
d10 <- ds %>% summarise( col = "d10n",
                         missing = (sum(is.na(d10n)) / nrow(ds))*100,
                         diff=length(unique(ds$d10n)),
                         max=max(d10n, na.rm=TRUE),
                         min=min(d10n, na.rm=TRUE),
                         avg=mean(d10n, na.rm=TRUE),
                         med=median(d10n, na.rm=TRUE),
                         var=var(d10n, na.rm=TRUE),
                         sd=sd(d10n, na.rm=TRUE)
)
d11 <- ds %>% summarise( col = "d11n",
                         missing = (sum(is.na(d11n)) / nrow(ds))*100,
                         diff=length(unique(ds$d11n)),
                         max=max(d11n, na.rm=TRUE),
                         min=min(d11n, na.rm=TRUE),
                         avg=mean(d11n, na.rm=TRUE),
                         med=median(d11n, na.rm=TRUE),
                         var=var(d11n, na.rm=TRUE),
                         sd=sd(d11n, na.rm=TRUE)
)
d12 <- ds %>% summarise( col = "d12n",
                         missing = (sum(is.na(d12n)) / nrow(ds))*100,
                         diff=length(unique(ds$d12n)),
                         max=max(d12n, na.rm=TRUE),
                         min=min(d12n, na.rm=TRUE),
                         avg=mean(d12n, na.rm=TRUE),
                         med=median(d12n, na.rm=TRUE),
                         var=var(d12n, na.rm=TRUE),
                         sd=sd(d12n, na.rm=TRUE)
)
d13 <- ds %>% summarise( col = "d13n",
                         missing = (sum(is.na(d13n)) / nrow(ds))*100,
                         diff=length(unique(ds$d13n)),
                         max=max(d13n, na.rm=TRUE),
                         min=min(d13n, na.rm=TRUE),
                         avg=mean(d13n, na.rm=TRUE),
                         med=median(d13n, na.rm=TRUE),
                         var=var(d13n, na.rm=TRUE),
                         sd=sd(d13n, na.rm=TRUE)
)
d14 <- ds %>% summarise( col = "d14n",
                         missing = (sum(is.na(d14n)) / nrow(ds))*100,
                         diff=length(unique(ds$d14n)),
                         max=max(d14n, na.rm=TRUE),
                         min=min(d14n, na.rm=TRUE),
                         avg=mean(d14n, na.rm=TRUE),
                         med=median(d14n, na.rm=TRUE),
                         var=var(d14n, na.rm=TRUE),
                         sd=sd(d14n, na.rm=TRUE)
)

d15 <- ds %>% summarise( col = "d15n",
                         missing = (sum(is.na(d15n)) / nrow(ds))*100,
                         diff=length(unique(ds$d15n)),
                         max=max(d15n, na.rm=TRUE),
                         min=min(d15n, na.rm=TRUE),
                         avg=mean(d15n, na.rm=TRUE),
                         med=median(d15n, na.rm=TRUE),
                         var=var(d15n, na.rm=TRUE),
                         sd=sd(d15n, na.rm=TRUE)
)

d <- full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(full_join(
  d1, d2),d3),d4),d5),d6),d7),d8),d9),d10),d11),d12),d13),d14),d15)
d

# M1-M9 matches
m <- select(ds, isFraud, M1, M2, M3, M4, M5, M6, M7, M8, M9)
uniq <- (nrow(unique(m)) / nrow(ds)) * 100
# there's only 0.63% of unique observations in M1-M9 and as we seen above, 
#the fraudulent cases are about 3.5%,
#so we can't use it to distinguish between fraudulent and nonfraudulent
# we can drop them.
# ds <- subset(ds, select=-c(47:55)) # done at the top of the file
m <- m %>% replace_with_na_all(~.x == '')

d <- group_by(m, M1) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M1, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M1, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))


d <- group_by(m, M2) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M2, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M2, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M3) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M3, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M3, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M4) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M4, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M4, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M5) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M5, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M5, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M6) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M6, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M6, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M7) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M7, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M7, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M8) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M8, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M8, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))

d <- group_by(m, M9) %>% mutate(fraud=sum(isFraud), notfraud=sum(isFraud==0), n=nrow(m)) %>% select(M9, fraud, notfraud, n)
d <- unique(d)
ggplot(d) + aes(x=M9, y=(fraud/n)*100) + geom_col() +
  scale_y_continuous(name="Percentage of fraudulent transactions",breaks=seq(0,3,0.1))


# V1-V338 engineered features
v <- select(ds, c(47:385)); v
data_na[56:394]

summary(v)
# there's a lot of variables in which we don't know the meaning and seem to be correlated, 
# so let's reduce the number of variables through PCA

v[is.na(v)] = -999
pca <- prcomp(v)
biplot(pca)

# USE PCA TO REDUCE VARIABLES
