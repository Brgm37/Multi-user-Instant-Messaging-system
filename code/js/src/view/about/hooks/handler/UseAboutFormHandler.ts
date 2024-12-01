import {Dev} from "../states/AboutState";

export type UseAboutFormHandler = {
    onSelectDev: (dev: Dev) => void,
    onToggleExpandBio: (dev: Dev) => void,
    onAcessGithub: () => void,
    onAcessLinkedIn: () => void,
}
