package br.gov.rj.teresopolis.prefeitura.domain.security;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import br.gov.rj.teresopolis.prefeitura.domain.Orgao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Usuario")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "usuarioId", scope = User.class)

public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usu_cd_id")
	private Integer id;

	@Size(max = 20)
	@Column(name = "usu_tx_nome_usuario", unique = true)
	@NotBlank(message = "O nome de usuario não pode ser nulo")
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank(message = "A senha não pode ser nula")
	@Size(min = 8, message = "A senha deve ter mais que 8 caracteres")
	@Column(name = "usu_tx_senha")
	private String password;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@ManyToOne
	@JoinColumn(name="fk_orgao_id",referencedColumnName="org_cd_id" )
    private Orgao orgao;

	public User() {
	}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	
	public Integer getId() {
		return id;
	}

	public User( String username,
			 String email,
			 String password,
			Orgao orgao) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.orgao = orgao;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

}