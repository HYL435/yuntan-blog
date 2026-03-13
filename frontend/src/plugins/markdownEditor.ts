import VMdEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';
import Codemirror from 'codemirror';
import 'codemirror/lib/codemirror.css';

VMdEditor.use(githubTheme);
VMdEditor.Codemirror = Codemirror;

export default VMdEditor;
