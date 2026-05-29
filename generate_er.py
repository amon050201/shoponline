"""Generate ER diagram with Chinese labels"""
from graphviz import Digraph

dot = Digraph(name='ER_Diagram', format='png', engine='dot')
dot.attr(rankdir='LR', splines='polyline', nodesep='0.3', ranksep='0.6')
dot.attr('node', shape='record', fontname='Microsoft YaHei', fontsize='9')
dot.attr('edge', fontname='Microsoft YaHei', fontsize='8')

C = {'B':'#dae8fc','G':'#d5e8d4','O':'#ffe6cc','P':'#e1d5e7','R':'#f8cecc','Y':'#fff2cc','X':'#f5f5f5'}

# === 实体 ===
dot.node('users', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['B']}" colspan="2"><b>用户表 users</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>唯一</td><td>username 用户名</td></tr>
<tr><td></td><td>password_hash 密码</td></tr>
<tr><td></td><td>email 邮箱</td></tr>
<tr><td></td><td>phone 手机号</td></tr>
<tr><td></td><td>role_type 角色(0顾客1商家2管理员)</td></tr>
<tr><td></td><td>status 状态(0禁用1启用)</td></tr>
</table>>''')

dot.node('category', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="2"><b>分类表 category</b></td></tr>
<tr><td>主键</td><td>id INT</td></tr>
<tr><td></td><td>name 分类名称</td></tr>
<tr><td></td><td>parent_id 父分类ID</td></tr>
<tr><td></td><td>sort_order 排序</td></tr>
</table>>''')

dot.node('product', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['O']}" colspan="2"><b>商品表 product</b></td></tr>
<tr><td>主键</td><td>id INT</td></tr>
<tr><td>外键</td><td>category_id 分类ID</td></tr>
<tr><td>外键</td><td>merchant_id 商家ID</td></tr>
<tr><td></td><td>name 商品名称</td></tr>
<tr><td></td><td>price 售价</td></tr>
<tr><td></td><td>stock 库存</td></tr>
<tr><td></td><td>brand 品牌</td></tr>
<tr><td></td><td>status 状态(0下架1上架)</td></tr>
</table>>''')

dot.node('orders', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['P']}" colspan="2"><b>订单表 orders</b></td></tr>
<tr><td>主键</td><td>id INT</td></tr>
<tr><td>唯一</td><td>order_no 订单号</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td></td><td>total_amount 总金额</td></tr>
<tr><td></td><td>status 状态(0待付1已付2已发3完成4取消)</td></tr>
<tr><td></td><td>payment_method 支付方式</td></tr>
</table>>''')

dot.node('order_item', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['P']}" colspan="2"><b>订单项表 order_item</b></td></tr>
<tr><td>主键</td><td>id INT</td></tr>
<tr><td>外键</td><td>order_id 订单ID</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
<tr><td></td><td>product_name 商品名</td></tr>
<tr><td></td><td>price 单价</td></tr>
<tr><td></td><td>quantity 数量</td></tr>
</table>>''')

dot.node('cart', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="2"><b>购物车表 cart</b></td></tr>
<tr><td>主键</td><td>id INT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
<tr><td></td><td>quantity 数量</td></tr>
<tr><td></td><td>selected 是否选中</td></tr>
</table>>''')

dot.node('address', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['B']}" colspan="2"><b>地址表 address</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td></td><td>receiver_name 收货人</td></tr>
<tr><td></td><td>receiver_phone 电话</td></tr>
<tr><td></td><td>region 地区</td></tr>
<tr><td></td><td>detail_address 详细地址</td></tr>
</table>>''')

dot.node('favorite', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="2"><b>收藏表 favorite</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
</table>>''')

dot.node('review', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="2"><b>评价表 review</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
<tr><td>外键</td><td>order_id 订单ID</td></tr>
<tr><td></td><td>rating 评分</td></tr>
<tr><td></td><td>content 评价内容</td></tr>
</table>>''')

dot.node('exchange_order', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['X']}" colspan="2"><b>交换订单表 exchange_order</b></td></tr>
<tr><td>主键</td><td>id INT</td></tr>
<tr><td>唯一</td><td>order_no 交换单号</td></tr>
<tr><td>外键</td><td>initiator_id 发起方ID</td></tr>
<tr><td>外键</td><td>receiver_id 接收方ID</td></tr>
<tr><td>外键</td><td>initiator_product_id 发起方商品ID</td></tr>
<tr><td>外键</td><td>receiver_product_id 接收方商品ID</td></tr>
<tr><td></td><td>price_difference 差价</td></tr>
<tr><td></td><td>status 状态</td></tr>
</table>>''')

dot.node('exchange_valuation', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['X']}" colspan="2"><b>交换估值表 exchange_valuation</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>exchange_order_id 交换订单ID</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
<tr><td></td><td>ai_estimated_value AI估值</td></tr>
</table>>''')


dot.node('user_behavior', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['P']}" colspan="2"><b>用户行为表 user_behavior</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
<tr><td></td><td>action 行为(浏览/搜索/加购/购买)</td></tr>
</table>>''')

dot.node('price_history', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['O']}" colspan="2"><b>价格历史表 price_history</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>product_id 商品ID</td></tr>
<tr><td></td><td>price 价格快照</td></tr>
<tr><td></td><td>recorded_at 记录时间</td></tr>
</table>>''')

dot.node('fraud_alert', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['R']}" colspan="2"><b>欺诈告警表 fraud_alert</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td></td><td>alert_type 告警类型</td></tr>
<tr><td></td><td>risk_score 风险评分</td></tr>
<tr><td></td><td>severity 严重程度(1低2中3高)</td></tr>
</table>>''')

dot.node('notification', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="2"><b>通知表 notification</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td></td><td>title 标题</td></tr>
<tr><td></td><td>content 内容</td></tr>
<tr><td></td><td>type 类型(系统/订单/促销)</td></tr>
<tr><td></td><td>is_read 已读</td></tr>
</table>>''')

dot.node('search_history', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="2"><b>搜索历史表 search_history</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td></td><td>keyword 关键词</td></tr>
</table>>''')

dot.node('feedback', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="2"><b>反馈表 feedback</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td>外键</td><td>user_id 用户ID</td></tr>
<tr><td></td><td>content 反馈内容</td></tr>
<tr><td></td><td>status 状态(0待处理1已处理2已回复)</td></tr>
</table>>''')

dot.node('banner', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="2"><b>轮播图表 banner</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td></td><td>title 标题</td></tr>
<tr><td></td><td>image_url 图片链接</td></tr>
<tr><td></td><td>link_url 跳转链接</td></tr>
</table>>''')

dot.node('operation_log', f'''<
<table border="0" cellborder="1" cellspacing="0" cellpadding="2">
<tr><td bgcolor="{C['G']}" colspan="2"><b>操作日志表 operation_log</b></td></tr>
<tr><td>主键</td><td>id BIGINT</td></tr>
<tr><td></td><td>user_id 操作用户ID</td></tr>
<tr><td></td><td>username 用户名</td></tr>
<tr><td></td><td>module 模块</td></tr>
<tr><td></td><td>operation 操作</td></tr>
<tr><td></td><td>result 结果</td></tr>
</table>>''')

# === 关系连线 ===
E = dot.edge
E('category', 'product', xlabel='1:N  分类', arrowhead='crow', color='#82b366')
E('users', 'product', xlabel='1:N  商家', arrowhead='crow', color='#6c8ebf')
E('users', 'orders', xlabel='1:N  下单', arrowhead='crow', color='#6c8ebf')
E('orders', 'order_item', xlabel='1:N  订单明细', arrowhead='crow', color='#9673a6')
E('product', 'order_item', xlabel='1:N  购买商品', arrowhead='crow', color='#d79b00')
E('product', 'cart', xlabel='1:N  加购', arrowhead='crow', color='#d79b00')
E('users', 'cart', xlabel='1:N  购物车', arrowhead='crow', color='#6c8ebf', style='dashed')
E('users', 'address', xlabel='1:N  收货地址', arrowhead='crow', color='#6c8ebf')
E('users', 'favorite', xlabel='1:N  收藏', arrowhead='crow', color='#6c8ebf', style='dashed')
E('product', 'favorite', xlabel='1:N  被收藏', arrowhead='crow', color='#d79b00', style='dashed')
E('users', 'review', xlabel='1:N  发表评价', arrowhead='crow', color='#6c8ebf', style='dashed')
E('product', 'review', xlabel='1:N  被评价', arrowhead='crow', color='#d79b00', style='dashed')
E('orders', 'review', xlabel='1:N  订单评价', arrowhead='crow', color='#9673a6', style='dashed')
E('users', 'exchange_order', xlabel='发起方', arrowhead='crow', color='#6c8ebf', style='dashed')
E('users', 'exchange_order', xlabel='接收方', arrowhead='crow', color='#6c8ebf', style='dashed')
E('product', 'exchange_order', xlabel='发起商品', arrowhead='crow', color='#d79b00', style='dashed')
E('product', 'exchange_order', xlabel='接收商品', arrowhead='crow', color='#d79b00', style='dashed')
E('exchange_order', 'exchange_valuation', xlabel='1:N  估值', arrowhead='crow', color='#666666')
E('product', 'exchange_valuation', xlabel='1:N  被估值', arrowhead='crow', color='#d79b00', style='dashed')
E('users', 'user_behavior', xlabel='1:N  行为', arrowhead='crow', color='#6c8ebf', style='dashed')
E('product', 'user_behavior', xlabel='1:N  行为对象', arrowhead='crow', color='#d79b00', style='dashed')
E('product', 'price_history', xlabel='1:N  价格记录', arrowhead='crow', color='#d79b00', style='dashed')
E('users', 'fraud_alert', xlabel='1:N  风险告警', arrowhead='crow', color='#6c8ebf', style='dashed')
E('users', 'notification', xlabel='1:N  通知', arrowhead='crow', color='#6c8ebf', style='dashed')
E('users', 'search_history', xlabel='1:N  搜索记录', arrowhead='crow', color='#6c8ebf', style='dashed')
E('users', 'feedback', xlabel='1:N  反馈', arrowhead='crow', color='#6c8ebf', style='dashed')

dot.render('c:/ideaProject/demo1/ER_DIAGRAM', cleanup=True, view=False)
print('Done: ER_DIAGRAM.png')
