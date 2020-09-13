package IOTGateConsole.controller.apigate.bean;

import java.util.Date;

/**
 * 对应于bootshiro资源类
 * @Description: 
 * @author  hejuanjuan
 * @date: 2020年4月19日
 */

public class AuthResource {
	/**
	 * 资源ID
	 */
    private Integer id;

    /**
     * 资源名称(资源编码)
     */
    private String code;
    /**
     * 资源描述
     */
    private String name;

    /**
     * 父资源ID
     */
    private Integer parentId;

    /**
     * 接口地址URL
     */
    private String uri;

    /**
     * 类型 
     */
    private Short type;

    /**
     * 访问方式 GET POST PUT DELETE PATCH
     */
    private String method;
    
    /**
     * 网关是否鉴权 
     */
    private int needAuth;
    
    /**
     * 路由信息
     */
    private String routeInfo;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态   
     */
    private Short status=1;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri == null ? null : uri.trim();
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }
    
    public int getNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(int needAuth) {
		this.needAuth = needAuth;
	}

	public String getRouteInfo() {
		return routeInfo;
	}

	public void setRouteInfo(String routeInfo) {
		this.routeInfo = routeInfo;
	}

	public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}