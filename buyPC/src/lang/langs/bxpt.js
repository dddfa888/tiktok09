import viLocale from 'element-ui/lib/locale/lang/pt-br'
import home from './bxpt/home'

const bxpt = {
    language: 'Português',
    message: {
        'home': home,
    },
    '手机号码最小长度为8位': 'O comprimento mínimo do número de telefone celular é de 8 dígitos',
    '邮箱账号名称最小长度为6位': 'O comprimento mínimo do nome da conta de e-mail é de 6 caracteres',
    '邮箱只能包含英文，数字等字符': 'O e-mail só pode conter caracteres em inglês, números e outros caracteres',
    '请选择正确的收货地址': 'Por favor, selecione o endereço de entrega correto',
    ...viLocale
}
export default bxpt
