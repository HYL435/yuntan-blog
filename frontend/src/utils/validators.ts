export const isValidEmail = (email: string): boolean => {
  if (!email) return false;
  const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
  return emailRegex.test(email);
};

export const isValidUsername = (username: string): boolean => {
  if (!username) return false;
  const usernameRegex = /^[A-Za-z0-9_]{3,20}$/;
  return usernameRegex.test(username);
};

export const isValidPhone = (phone: string): boolean => {
  if (!phone) return false;
  const phoneRegex = /^\+[1-9]\d{1,14}$/;
  return phoneRegex.test(phone);
};

export const isValidPassword = (password: string): boolean => {
  if (!password) return false;
  if (password.length < 8 || password.length > 20) return false;
  const hasLetter = /[A-Za-z]/.test(password);
  const hasNumber = /\d/.test(password);
  return hasLetter && hasNumber;
};

type RegisterUserInput = {
  username: string;
  email: string;
  password: string;
  phone?: string;
};

type LoginUserInput = {
  username?: string;
  email?: string;
  password: string;
};

export const validateRegisterUser = (input: RegisterUserInput): { valid: boolean; message?: string } => {
  const username = input.username?.trim() ?? '';
  const email = input.email?.trim() ?? '';
  const password = input.password ?? '';
  const phone = input.phone?.trim() ?? '';

  if (!username || !email || !password) {
    return { valid: false, message: '用户名、邮箱、密码为必填项。' };
  }

  if (!isValidUsername(username)) {
    return { valid: false, message: '用户名需为 3-20 位，仅包含字母、数字、下划线。' };
  }

  if (!isValidEmail(email)) {
    return { valid: false, message: '邮箱格式不正确。' };
  }

  if (!isValidPassword(password)) {
    return { valid: false, message: '密码需为 8-20 位，且同时包含字母和数字。' };
  }

  if (phone && !isValidPhone(phone)) {
    return { valid: false, message: '手机号格式不正确，请使用国际格式，例如 +8613800138000。' };
  }

  return { valid: true };
};

export const validateLoginUser = (input: LoginUserInput): { valid: boolean; message?: string } => {
  const username = input.username?.trim() ?? '';
  const email = input.email?.trim() ?? '';
  const password = input.password ?? '';

  if (!password) {
    return { valid: false, message: '密码为必填项。' };
  }

  if (!username && !email) {
    return { valid: false, message: '用户名或邮箱至少填写一个。' };
  }

  if (username && !isValidUsername(username)) {
    return { valid: false, message: '用户名需为 3-20 位，仅包含字母、数字、下划线。' };
  }

  if (email && !isValidEmail(email)) {
    return { valid: false, message: '邮箱格式不正确。' };
  }

  return { valid: true };
};
