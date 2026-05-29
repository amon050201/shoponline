"""Generate individual table structure diagram"""
from graphviz import Digraph

dot = Digraph(name='Tables', format='png', engine='dot')
dot.attr(rankdir='TB', splines='line', nodesep='0.5', ranksep='1.0')
dot.attr('node', shape='record', fontname='Microsoft YaHei', fontsize='9')

C = {'U':'#dae8fc','P':'#ffe6cc','O':'#e1d5e7','R':'#f8cecc','G':'#d5e8d4','X':'#f5f5f5'}

dot.node('users', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['U']}" colspan="3"><b>用户表 users</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>UK</td><td>username</td><td>VARCHAR(50) 用户名</td></tr>
<tr><td></td><td>password_hash</td><td>VARCHAR(100) BCrypt密码</td></tr>
<tr><td></td><td>email</td><td>VARCHAR(50) 邮箱</td></tr>
<tr><td></td><td>phone</td><td>VARCHAR(20) 手机号</td></tr>
<tr><td></td><td>role_type</td><td>TINYINT 角色(0顾客1商家2管理员)</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态(0禁用1启用)</td></tr>
<tr><td></td><td>created_at</td><td>DATETIME 注册时间</td></tr>
<tr><td></td><td>updated_at</td><td>DATETIME 更新时间</td></tr>
</table>>''')

dot.node('address', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['U']}" colspan="3"><b>收货地址表 address</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td></td><td>receiver_name</td><td>VARCHAR(50) 收货人姓名</td></tr>
<tr><td></td><td>receiver_phone</td><td>VARCHAR(20) 收货人电话</td></tr>
<tr><td></td><td>region</td><td>VARCHAR(200) 所在地区</td></tr>
<tr><td></td><td>detail_address</td><td>VARCHAR(500) 详细地址</td></tr>
<tr><td></td><td>is_default</td><td>TINYINT 是否默认</td></tr>
<tr><td></td><td>label</td><td>VARCHAR(50) 标签</td></tr>
</table>>''')

dot.node('category', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="3"><b>分类表 category</b></td></tr>
<tr><td>PK</td><td>id</td><td>INT 自增主键</td></tr>
<tr><td></td><td>name</td><td>VARCHAR(100) 分类名称</td></tr>
<tr><td></td><td>description</td><td>VARCHAR(500) 描述</td></tr>
<tr><td></td><td>parent_id</td><td>INT 父分类ID</td></tr>
<tr><td></td><td>sort_order</td><td>INT 排序</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态</td></tr>
</table>>''')

dot.node('product', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['P']}" colspan="3"><b>商品表 product</b></td></tr>
<tr><td>PK</td><td>id</td><td>INT 自增主键</td></tr>
<tr><td>FK</td><td>category_id</td><td>INT 分类ID</td></tr>
<tr><td>FK</td><td>merchant_id</td><td>BIGINT 商家ID</td></tr>
<tr><td></td><td>name</td><td>VARCHAR(200) 商品名称</td></tr>
<tr><td></td><td>description</td><td>TEXT 商品描述</td></tr>
<tr><td></td><td>price</td><td>DECIMAL(10,2) 售价</td></tr>
<tr><td></td><td>original_price</td><td>DECIMAL(10,2) 原价</td></tr>
<tr><td></td><td>stock</td><td>INT 库存</td></tr>
<tr><td></td><td>image_url</td><td>VARCHAR(500) 主图</td></tr>
<tr><td></td><td>images</td><td>TEXT 多图JSON</td></tr>
<tr><td></td><td>brand</td><td>VARCHAR(100) 品牌</td></tr>
<tr><td></td><td>model</td><td>VARCHAR(100) 型号</td></tr>
<tr><td></td><td>weight</td><td>DECIMAL(8,2) 重量</td></tr>
<tr><td></td><td>dimensions</td><td>VARCHAR(100) 尺寸</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态(0下架1上架)</td></tr>
<tr><td></td><td>sales_count</td><td>INT 销量</td></tr>
<tr><td></td><td>view_count</td><td>INT 浏览量</td></tr>
<tr><td></td><td>product_condition</td><td>VARCHAR(50) AI:成色</td></tr>
<tr><td></td><td>usage_years</td><td>DECIMAL(4,1) AI:使用年限</td></tr>
<tr><td></td><td>accessories</td><td>TEXT AI:配件</td></tr>
<tr><td></td><td>ai_tags</td><td>TEXT AI:标签</td></tr>
</table>>''')

dot.node('price_history', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['P']}" colspan="3"><b>价格历史表 price_history</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td></td><td>price</td><td>DECIMAL(10,2) 价格快照</td></tr>
<tr><td></td><td>recorded_at</td><td>DATETIME 记录时间</td></tr>
</table>>''')

dot.node('orders', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['O']}" colspan="3"><b>订单表 orders</b></td></tr>
<tr><td>PK</td><td>id</td><td>INT 自增主键</td></tr>
<tr><td>UK</td><td>order_no</td><td>VARCHAR(50) 订单号</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td></td><td>total_amount</td><td>DECIMAL(10,2) 总金额</td></tr>
<tr><td></td><td>actual_amount</td><td>DECIMAL(10,2) 实付金额</td></tr>
<tr><td></td><td>shipping_fee</td><td>DECIMAL(10,2) 运费</td></tr>
<tr><td></td><td>discount_amount</td><td>DECIMAL(10,2) 优惠</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态(0待付1已付2已发3完成4取消)</td></tr>
<tr><td></td><td>payment_method</td><td>VARCHAR(50) 支付方式</td></tr>
<tr><td></td><td>transaction_id</td><td>VARCHAR(100) 流水号</td></tr>
<tr><td></td><td>payment_time</td><td>DATETIME 支付时间</td></tr>
<tr><td></td><td>shipping_address</td><td>VARCHAR(500) 收货地址</td></tr>
<tr><td></td><td>receiver_name</td><td>VARCHAR(100) 收货人</td></tr>
<tr><td></td><td>receiver_phone</td><td>VARCHAR(20) 收货电话</td></tr>
<tr><td></td><td>remark</td><td>VARCHAR(500) 备注</td></tr>
<tr><td></td><td>shipping_company</td><td>VARCHAR(100) 快递公司</td></tr>
<tr><td></td><td>tracking_number</td><td>VARCHAR(100) 快递单号</td></tr>
<tr><td></td><td>delivery_time</td><td>DATETIME 发货时间</td></tr>
</table>>''')

dot.node('order_item', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['O']}" colspan="3"><b>订单项表 order_item</b></td></tr>
<tr><td>PK</td><td>id</td><td>INT 自增主键</td></tr>
<tr><td>FK</td><td>order_id</td><td>INT 订单ID</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td></td><td>product_name</td><td>VARCHAR(200) 商品名快照</td></tr>
<tr><td></td><td>product_image</td><td>VARCHAR(500) 图片快照</td></tr>
<tr><td></td><td>price</td><td>DECIMAL(10,2) 单价快照</td></tr>
<tr><td></td><td>quantity</td><td>INT 数量</td></tr>
<tr><td></td><td>total_price</td><td>DECIMAL(10,2) 小计</td></tr>
</table>>''')

dot.node('cart', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="3"><b>购物车表 cart</b></td></tr>
<tr><td>PK</td><td>id</td><td>INT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td></td><td>quantity</td><td>INT 数量</td></tr>
<tr><td></td><td>selected</td><td>TINYINT 是否选中</td></tr>
</table>>''')

dot.node('favorite', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="3"><b>收藏表 favorite</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td></td><td>created_time</td><td>DATETIME 收藏时间</td></tr>
</table>>''')

dot.node('review', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="3"><b>评价表 review</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td>FK</td><td>order_id</td><td>INT 订单ID</td></tr>
<tr><td></td><td>rating</td><td>TINYINT 评分(1-5)</td></tr>
<tr><td></td><td>content</td><td>TEXT 评价内容</td></tr>
<tr><td></td><td>images</td><td>TEXT 图片</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态</td></tr>
</table>>''')

dot.node('user_behavior', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['O']}" colspan="3"><b>用户行为表 user_behavior</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td></td><td>action</td><td>VARCHAR(20) 行为(view/search/add_cart/purchase)</td></tr>
<tr><td></td><td>keyword</td><td>VARCHAR(200) 搜索关键词</td></tr>
<tr><td></td><td>duration</td><td>INT 停留时长(秒)</td></tr>
</table>>''')

dot.node('fraud_alert', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="3"><b>欺诈告警表 fraud_alert</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td></td><td>alert_type</td><td>VARCHAR(50) 告警类型</td></tr>
<tr><td></td><td>description</td><td>VARCHAR(500) 描述</td></tr>
<tr><td></td><td>risk_score</td><td>DOUBLE 风险评分</td></tr>
<tr><td></td><td>severity</td><td>TINYINT 严重程度(1低2中3高)</td></tr>
<tr><td></td><td>resolved</td><td>TINYINT 是否已处理</td></tr>
<tr><td></td><td>reference_id</td><td>VARCHAR(100) 关联ID</td></tr>
</table>>''')

dot.node('exchange_order', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['X']}" colspan="3"><b>交换订单表 exchange_order</b></td></tr>
<tr><td>PK</td><td>id</td><td>INT 自增主键</td></tr>
<tr><td>UK</td><td>order_no</td><td>VARCHAR(50) 交换单号</td></tr>
<tr><td>FK</td><td>initiator_id</td><td>BIGINT 发起方ID</td></tr>
<tr><td>FK</td><td>receiver_id</td><td>BIGINT 接收方ID</td></tr>
<tr><td>FK</td><td>initiator_product_id</td><td>INT 发起方商品ID</td></tr>
<tr><td>FK</td><td>receiver_product_id</td><td>INT 接收方商品ID</td></tr>
<tr><td></td><td>price_difference</td><td>DECIMAL(10,2) 差价</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态(0待确认1已确认2完成3取消4拒绝)</td></tr>
<tr><td></td><td>payment_status</td><td>TINYINT 支付状态</td></tr>
<tr><td></td><td>initiator_address</td><td>VARCHAR(500) 发起方地址</td></tr>
<tr><td></td><td>receiver_address</td><td>VARCHAR(500) 接收方地址</td></tr>
<tr><td></td><td>ai_initiator_valuation</td><td>DECIMAL(10,2) AI发起方估值</td></tr>
<tr><td></td><td>ai_receiver_valuation</td><td>DECIMAL(10,2) AI接收方估值</td></tr>
<tr><td></td><td>recommended_difference</td><td>DECIMAL(10,2) AI建议差价</td></tr>
<tr><td></td><td>fraud_risk_score</td><td>DOUBLE AI欺诈风险分</td></tr>
</table>>''')

dot.node('exchange_valuation', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['X']}" colspan="3"><b>交换估值表 exchange_valuation</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>exchange_order_id</td><td>INT 交换订单ID</td></tr>
<tr><td>FK</td><td>product_id</td><td>INT 商品ID</td></tr>
<tr><td></td><td>scenario</td><td>VARCHAR(50) 估值场景</td></tr>
<tr><td></td><td>ai_estimated_value</td><td>DECIMAL(10,2) AI估值</td></tr>
<tr><td></td><td>market_range_low</td><td>DECIMAL(10,2) 市场价低</td></tr>
<tr><td></td><td>market_range_high</td><td>DECIMAL(10,2) 市场价高</td></tr>
<tr><td></td><td>condition_assessment</td><td>VARCHAR(100) 成色评估</td></tr>
<tr><td></td><td>market_analysis</td><td>TEXT 市场分析</td></tr>
<tr><td></td><td>provider</td><td>VARCHAR(50) AI服务商</td></tr>
<tr><td></td><td>factors</td><td>JSON 评估因子</td></tr>
</table>>''')

dot.node('notification', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="3"><b>通知表 notification</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td></td><td>title</td><td>VARCHAR(200) 标题</td></tr>
<tr><td></td><td>content</td><td>VARCHAR(1000) 内容</td></tr>
<tr><td></td><td>type</td><td>VARCHAR(50) 类型(system/order/promotion)</td></tr>
<tr><td></td><td>reference_id</td><td>VARCHAR(100) 关联ID</td></tr>
<tr><td></td><td>is_read</td><td>TINYINT 是否已读</td></tr>
</table>>''')

dot.node('search_history', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="3"><b>搜索历史表 search_history</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td></td><td>keyword</td><td>VARCHAR(200) 搜索关键词</td></tr>
</table>>''')

dot.node('feedback', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="3"><b>反馈表 feedback</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td>FK</td><td>user_id</td><td>BIGINT 用户ID</td></tr>
<tr><td></td><td>content</td><td>TEXT 反馈内容</td></tr>
<tr><td></td><td>contact</td><td>VARCHAR(100) 联系方式</td></tr>
<tr><td></td><td>images</td><td>TEXT 图片</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态(0待处理1已处理2已回复)</td></tr>
<tr><td></td><td>reply</td><td>TEXT 回复内容</td></tr>
</table>>''')

dot.node('banner', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="3"><b>Banner表 banner</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td></td><td>title</td><td>VARCHAR(100) 标题</td></tr>
<tr><td></td><td>image_url</td><td>VARCHAR(500) 图片链接</td></tr>
<tr><td></td><td>link_url</td><td>VARCHAR(500) 跳转链接</td></tr>
<tr><td></td><td>sort_order</td><td>INT 排序</td></tr>
<tr><td></td><td>status</td><td>TINYINT 状态</td></tr>
</table>>''')

dot.node('operation_log', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="3"><b>操作日志表 operation_log</b></td></tr>
<tr><td>PK</td><td>id</td><td>BIGINT 自增主键</td></tr>
<tr><td></td><td>user_id</td><td>BIGINT 操作用户ID</td></tr>
<tr><td></td><td>username</td><td>VARCHAR(50) 操作用户名</td></tr>
<tr><td></td><td>module</td><td>VARCHAR(50) 模块</td></tr>
<tr><td></td><td>operation</td><td>VARCHAR(100) 操作</td></tr>
<tr><td></td><td>method</td><td>VARCHAR(200) 方法签名</td></tr>
<tr><td></td><td>params</td><td>TEXT 参数</td></tr>
<tr><td></td><td>ip</td><td>VARCHAR(50) IP地址</td></tr>
<tr><td></td><td>result</td><td>VARCHAR(500) 结果</td></tr>
<tr><td></td><td>duration</td><td>BIGINT 耗时(ms)</td></tr>
</table>>''')

dot.render('c:/ideaProject/demo1/TABLE_STRUCTURES', cleanup=True, view=False)
print('Done: TABLE_STRUCTURES.png')
