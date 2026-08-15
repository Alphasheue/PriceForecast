PriceForecast - 基于 LSTM + GRU 双循环神经网络的价格预测系统

项目简介

本项目是一套端到端的价格预测解决方案，通过 Python 爬虫采集实时市场数据，利用 LSTM/GRU 深度学习模型进行趋势预测，最终通过 Java Web​ 技术实现结果可视化与交互。
覆盖数据采集→模型训练→Web 可视化全流程：
数据采集：Python Scrapy 爬虫获取商品历史/实时价格数据。
模型训练：基于 Keras/TensorFlow 实现 LSTM（长短期记忆网络）和 GRU（门控循环单元）双模型，对比预测效果。
Web 展示：Java (SpringBoot/JavaFX) 构建后端服务 + 前端页面，可视化预测结果与历史趋势。

技术栈

后端/模型​
	
Python 3.x + Scrapy（爬虫）+ Keras/TensorFlow（LSTM/GRU）+ NumPy/Pandas（数据处理）


后端/服务​
	
Java 8 + Maven + SpringBoot（或 JavaFX）+ JDBC（数据库操作）


前端/可视化​
	
JSP + CSS + HTML


数据库​
	
MySQL/PostgreSQL（存储历史价格、预测结果）


1. 环境准备
安装 JDK 1.8+、Maven 3.6+、Python 3.7+、MySQL 5.7+。
克隆仓库：
git clone https://github.com/Alphasheue/PriceForecast.git
cd PriceForecast
2. 数据采集（Python 爬虫）
进入 src/main/java/com/sheue/app/ml/dataset 目录（或对应 Python 爬虫目录），执行爬虫脚本：
# 安装 Python 依赖
pip install scrapy numpy pandas

# 运行 Scrapy 爬虫（示例命令，需根据实际爬虫入口调整）
scrapy crawl price_spider -o data.csv
爬虫会将数据保存为 data.csv（或数据库），供后续模型训练使用。
3. 模型训练（LSTM/GRU 双模型）
进入 src/main/java/com/sheue/app/ml/rnn 目录，执行模型训练脚本：
# 训练 LSTM 模型
python train_lstm.py --data data.csv --epochs 100 --batch_size 32

# 训练 GRU 模型
python train_gru.py --data data.csv --epochs 100 --batch_size 32
训练完成后，模型文件（如 lstm_model.h5、gru_model.h5）会保存在 ml/rnn/models 目录（需自行创建）。
4. 后端服务（Java Web）
方式 1：IDE 直接运行
用 IntelliJ IDEA 打开项目，导入 pom.xml 依赖。
运行 src/main/java/com/sheue/app/controller/MainServlet.java（或 SpringBoot 启动类）。
方式 2：Maven 打包运行
mvn clean package
java -jar target/PriceForecast.jar
5. 前端访问
启动服务后，访问：
http://localhost:8080
页面包含历史价格趋势图、LSTM/GRU 预测结果对比、数据查询等功能。


项目结构（核心目录）
PriceForecast/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sheue/app/
│   │   │       ├── bean/       # 实体类（如 Data.java）
│   │   │       ├── controller/ # 控制层（MainServlet.java）
│   │   │       ├── dao/        # 数据访问层（PriceDAO.java）
│   │   │       ├── db/         # 数据库连接（JDBCConnection.java）
│   │   │       ├── ml/         # 机器学习核心（LSTM/GRU 实现）
│   │   │       │   ├── dataset/ # 数据处理（DataText.java）
│   │   │       │   ├── layers/  # 网络层（MatIniter.java）
│   │   │       │   └── rnn/     # RNN 模型（LSTM/GRU）
│   │   │       │       ├── gru/ # GRU 模型（GRU.java, GRUPredict.java）
│   │   │       │       └── lstm/# LSTM 模型（LSTM.java, LSTMPredict.java）
│   │   │       ├── utils/      # 工具类（Activer.java, LossFunction.java）
│   │   │       └── web/        # Web 资源（WEB-INF, jsp, css, image）
│   │   └── resources/          # 配置文件（generatorConfig.xml, log4j.properties）
│   └── test/                   # 测试代码
├── pom.xml                     # Maven 依赖配置
└── README.md                   # 项目说明

核心模块详解
1. 机器学习模块（ml/ 目录）
dataset/：数据预处理（DataText.java），实现数据清洗、归一化、序列划分（适配 RNN 输入格式）。
layers/：网络层初始化（MatIniter.java），封装权重初始化、激活函数（如 ReLU、Sigmoid）。
rnn/gru/：
GRU.java：定义 GRU 网络结构（更新门、重置门、候选隐藏态）。
GRUPredict.java：加载 GRU 模型，对新数据进行价格预测。
rnn/lstm/：
LSTM.java：定义 LSTM 网络结构（输入门、遗忘门、输出门、细胞状态）。
LSTMPredict.java：加载 LSTM 模型，对新数据进行价格预测。
utils/：工具类（Activer.java 激活函数、LossFunction.java 损失函数）。

3. Web 模块（web/ 目录）
WEB-INF/：配置文件（web.xml）、Spring 配置（若有）。
jsp/：页面（index.jsp 首页、predict.jsp 预测页）。
css/：样式（style.css）。
image/：静态资源（background.jpg 背景图）。

4. 数据层（dao/ + db/）
PriceDAO.java：封装数据库操作（查询历史价格、插入预测结果）。
JDBCConnection.java：数据库连接池（或 JDBC 工具类）。


模型对比（LSTM vs GRU）
LSTM​
优势：长期依赖捕捉能力强，结构严谨
劣势：参数多，训练速度慢
适用场景：超长周期价格趋势预测

GRU​
优势：参数少，训练速度快，结构简洁
劣势：长期依赖捕捉能力略弱于 LSTM
适用场景：中短期价格预测、资源受限场景
